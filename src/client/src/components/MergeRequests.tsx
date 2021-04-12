import { useEffect, useRef, useState } from 'react'
import useSuspense from '../utils/useSuspense'
import dateConverter from '../utils/dateConverter'
import { noop } from 'lodash'
import classNames from '../utils/classNames'
import useProject from '../utils/useProject'
import { ICommit } from '../context/ProjectContext'

import Table from '../components/Table'
import Diff, { IDiffProps } from '../components/Diff'
import IgnoreBox from '../components/IgnoreBox'

import styles from '../css/MergeRequests.module.css'
import { IMergeRequest } from '../context/ProjectContext'

export interface IMergeRequestsProps {
  projectId: number
  memberId: number
}

type TTableData = {
  date: string
  title: string
  score: number
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
  const { Suspense, data: mergeRequests, error } = useSuspense<
    IMergeRequests[]
  >(
    (setData, setError) => {
      if (!project) {
        setError(new Error("Failed to load the member's data"))
      } else if (project !== 'LOADING') {
        console.log(project)
        setData(
          Object.values(project.mergeRequests).map(({ time, ...mr }) => {
            return {
              // TODO: left-align .toFixed(1) score.
              ...mr,
              time,
              date: dateConverter(time, true),
              ignore: <IgnoreBox onChange={noop} />,
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
            score,
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

      commits.forEach(({ score, time, message }) => {
        commitTableData.push({
          date: dateConverter(time, true),
          title: message,
          // TODO: left-align .toFixed(1) score.
          score,
          ignore: <IgnoreBox onChange={noop} />,
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
      Object.is(diffProps.data, selectedDiff?.data) ? undefined : diffProps
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
                const { id, files, message } = commits[i]
                viewDiffOf({
                  data: Object.values(files),
                  type: 'Commit',
                  id: id,
                  title: message,
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
