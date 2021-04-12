import { ReactNode, useContext, useEffect, useRef, useState } from 'react'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import classNames from '../utils/classNames'
import useProject from '../utils/useProject'
import {
  ICommit,
  IGNORE_COMMIT,
  IGNORE_MR,
  IGNORE_MR_FILE,
  ProjectContext,
} from '../context/ProjectContext'
import { IMergeRequest } from '../context/ProjectContext'
import fastDeepEquals from 'fast-deep-equal'

import Table from '../components/Table'
import Diff, { IDiffProps } from '../components/Diff'
import IgnoreBox from '../components/IgnoreBox'

import styles from '../css/MergeRequests.module.css'

export interface IMergeRequestsProps {
  projectId: number
  memberId: number
}

type TTableData = {
  date: string
  title: string
  score: ReactNode
  ignore: JSX.Element
}[]

interface IMergeRequests extends IMergeRequest {
  date: string
  ignore: JSX.Element
}

const sharedTableProps = {
  sortable: true,
  collapsible: true,
  classes: {
    container: styles.tableContainer,
    table: styles.table,
    header: styles.theader,
    data: styles.tdata,
    row: styles.row,
  },
}

const MergeRequests = ({ projectId, memberId }: IMergeRequestsProps) => {
  const [selectedMr, setSelectedMr] = useState<number>()
  const [selectedDiff, setSelectedDiff] = useState<IDiffProps>()
  const [commits, setCommits] = useState<ICommit[]>()
  const [tableMrs, setTableMrs] = useState<TTableData>([])
  const tableData = useRef<{ mrs?: TTableData; commits?: TTableData }>()

  const project = useProject()
  console.log(project)
  const { dispatch } = useContext(ProjectContext)

  const { Suspense, data: mergeRequests, error } = useSuspense<
    IMergeRequests[]
  >(
    (setData, setError) => {
      if (!project) {
        setError(new Error("Failed to load the member's data"))
      } else if (project !== 'LOADING') {
        setData(
          Object.values(project.mergeRequests).map(({ time, ...mr }) => {
            return {
              ...mr,
              time,
              date: dateConverter(time, true),
              ignore: (
                <IgnoreBox
                  onChange={event => {
                    const checked = (event.target as HTMLInputElement).checked
                    dispatch({
                      type: IGNORE_MR,
                      mrId: mr.mergeRequestId,
                      setIgnored: checked,
                    })
                  }}
                  checked={mr.isIgnored}
                />
              ),
            }
          })
        )
      }
    },
    [projectId, memberId, project]
  )

  useEffect(() => {
    if (mergeRequests?.length) {
      setTableMrs(
        mergeRequests.map(({ date, ignore, score, title }) => {
          return {
            date,
            title,
            score: <div className={styles.score}>{score.toFixed(1)}</div>,
            ignore,
          }
        })
      )
    }
  }, [mergeRequests])

  useEffect(() => {
    if (selectedMr !== undefined) {
      const commitTableData: TTableData = []
      const commits = Object.values(mergeRequests?.[selectedMr]?.commits ?? {})

      commits.forEach(({ score, time, message, mrId, id }) => {
        commitTableData.push({
          date: dateConverter(time, true),
          title: message,
          score: <div className={styles.score}>{score.toFixed(1)}</div>,
          ignore: (
            <IgnoreBox
              onChange={event => {
                const checked = (event.target as HTMLInputElement).checked
                dispatch({
                  type: IGNORE_COMMIT,
                  mrId,
                  commitId: id,
                  setIgnored: checked,
                })
              }}
            />
          ),
        })
      })

      tableData.current = {
        ...tableData.current,
        commits: commitTableData,
      }

      setCommits(commits)
    }
  }, [selectedMr])

  const viewDiffOf = (diffProps: IDiffProps) => {
    setSelectedDiff(
      fastDeepEquals(diffProps.data, selectedDiff?.data) ? undefined : diffProps
    )
  }

  return (
    <Suspense
      fallback="Loading Merge Requests..."
      error={error?.message ?? 'Unknown Error'}
    >
      <div
        className={classNames(
          styles.container,
          selectedDiff !== undefined && styles.showDiff
        )}
      >
        <div className={styles.diff}>
          {selectedDiff && <Diff {...selectedDiff} />}
        </div>
        <div className={styles.tables}>
          <Table
            {...sharedTableProps}
            title="Merge Requests"
            headers={['Date', 'Title', 'Score', 'Ignore?']}
            columnWidths={['6fr', '6fr', '1fr', '1fr']}
            onClick={(e, i) => {
              if (mergeRequests?.[i]) {
                const {
                  files,
                  mergeRequestId,
                  sumOfCommitsScore,
                  title,
                } = mergeRequests[i]
                setSelectedMr(mergeRequestId)
                viewDiffOf({
                  data: Object.values(files),
                  type: 'MR',
                  id: `#${mergeRequestId}`,
                  commitsScore: sumOfCommitsScore[memberId],
                  title,
                  ignore: (fileId: string, setIgnored: boolean) => {
                    dispatch({
                      type: IGNORE_MR_FILE,
                      fileId,
                      mrId: mergeRequestId,
                      setIgnored,
                    })
                  },
                })
              }
            }}
            data={tableMrs}
            maxHeight={400}
            startOpened
          />
          <Table
            {...sharedTableProps}
            isOpen={commits !== undefined}
            title={`Commits for MR #${selectedMr ?? ''}`}
            headers={['Date', 'Title', 'Score', 'Ignore?']}
            columnWidths={['6fr', '6fr', '1fr', '1fr']}
            onClick={(e, i) => {
              if (commits?.[i]) {
                const { id, files, message, mrId } = commits[i]
                viewDiffOf({
                  data: Object.values(files),
                  type: 'Commit',
                  id: id,
                  title: message,
                  ignore: (fileId: string, setIgnored: boolean) => {
                    dispatch({
                      type: IGNORE_MR_FILE,
                      fileId,
                      mrId,
                      setIgnored,
                    })
                  },
                })
              }
            }}
            data={tableData.current?.commits ?? []}
          />
        </div>
      </div>
    </Suspense>
  )
}

export default MergeRequests
