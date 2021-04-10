import {
  useEffect,
  useRef,
  useState,
  HTMLProps,
  MouseEventHandler,
  MouseEvent,
} from 'react'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { TCommitData, TMergeData } from '../types'
import dateConverter from '../utils/dateConverter'
import { noop } from 'lodash'
import classNames from '../utils/classNames'

import Table from '../components/Table'
import Diff, { IDiffProps } from '../components/Diff'

import styles from '../css/MergeRequests.module.css'

export interface IMergeRequestsProps {
  projectId: string
  memberId?: string
}

type TTableData = {
  date: string
  title: string
  view?: JSX.Element
  score: number
  ignore: JSX.Element
}[]

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

const eventStopper = (onClick?: (event: MouseEvent) => void) => {
  const handler: MouseEventHandler = event => {
    event.stopPropagation()
    onClick?.(event)
  }
  return handler
}

const IgnoreBox = ({
  onClick,
  className,
  ...props
}: HTMLProps<HTMLInputElement>) => (
  <input
    {...props}
    onClick={eventStopper(e => onClick?.(e as MouseEvent<HTMLInputElement>))}
    type="checkbox"
    className={classNames(styles.ignore, className)}
  />
)

const MergeRequests = ({ projectId, memberId }: IMergeRequestsProps) => {
  const [selectedMr, setSelectedMr] = useState<string>()
  const [selectedDiff, setSelectedDiff] = useState<IDiffProps>()
  const [commits, setCommits] = useState<TCommitData>()
  const tableData = useRef<{ mrs?: TTableData; commits?: TTableData }>()

  const { Suspense, data: mergeRequests, error } = useSuspense<TMergeData>(
    (setData, setError) => {
      jsonFetcher<TMergeData>(
        `/api/project/${projectId}/members/${memberId}/mergerequests`
      )
        .then(merges => {
          const mrTableData: TTableData = []
          merges.forEach(({ time, title, score }) => {
            mrTableData.push({
              date: dateConverter(time, true),
              title: title,
              score: score,
              ignore: <IgnoreBox onChange={noop} />,
            })
          })
          tableData.current = {
            ...tableData.current,
            mrs: mrTableData,
          }
          setData(merges)
        })
        .catch(onError(setError))
    },
    [projectId, memberId]
  )

  useEffect(() => {
    if (selectedMr !== undefined) {
      jsonFetcher<TCommitData>(
        `/api/project/${projectId}/mergerequest/${selectedMr}/commits`
      )
        .then(commits => {
          const commitTableData: TTableData = []

          commits.forEach(({ score, time, title }) => {
            commitTableData.push({
              date: dateConverter(time, true),
              title,
              score,
              ignore: <IgnoreBox onChange={noop} />,
            })
          })

          tableData.current = {
            ...tableData.current,
            commits: commitTableData,
          }

          setCommits(commits)
        })
        .catch(console.error)
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
                  commitsInfoInMergeRequest,
                  title,
                } = mergeRequests[i]
                console.log(mergeRequests[i])
                setSelectedMr(mergeRequestId)
                viewDiffOf({
                  data: files,
                  type: 'MR',
                  id: `#${mergeRequestId}`,
                  commits: commitsInfoInMergeRequest,
                  title,
                })
              }
            }}
            data={tableData.current?.mrs ?? []}
            maxHeight={400}
            startOpened
          />
          <Table
            {...sharedTableProps}
            isOpen={commits !== undefined}
            title={`Commits for MR ${selectedMr ?? ''}`}
            headers={['Date', 'Title', 'Score', 'Ignore?']}
            columnWidths={['6fr', '6fr', '1fr', '1fr']}
            onClick={(e, i) => {
              if (commits?.[i]) {
                const { id, files, message } = commits[i]
                viewDiffOf({
                  data: files,
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
