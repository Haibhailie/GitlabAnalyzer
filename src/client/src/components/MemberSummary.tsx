import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { IMemberData, IMemberStatData } from '../pages/Member'
import { ICommitData, IMergeData } from '../components/ActivityGraph'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

export interface IMemberSummaryProps {
  projectId: string
  memberData: IMemberData | undefined
}

const computeStats = (
  commitData: ICommitData[],
  mergeData: IMergeData[]
): IMemberStatData => {
  return {
    commits: commitData,
    mergeRequests: mergeData,
  } as IMemberStatData
}

const computeCommitScore = (commitData: ICommitData[] | undefined): number => {
  if (!commitData) {
    return 0
  }
  let commitScore = 0
  for (const commit of commitData) {
    const score = Math.floor(Math.random() * 100 + commit.author.length)
    commitScore += score
  }
  return commitScore
}

const computeMergeScore = (
  mergeRequestData: IMergeData[] | undefined
): number => {
  if (!mergeRequestData) {
    return 0
  }
  let mergeScore = 0
  for (const mergeRequest of mergeRequestData) {
    const score = Math.floor(Math.random() * 50 + mergeRequest.author.length)
    mergeScore += score
  }
  return mergeScore
}

const computeLinesAdded = (commitData: ICommitData[] | undefined): number => {
  if (!commitData) {
    return 0
  }
  let totalLineAdditions = 0
  for (const commit of commitData) {
    const lines = Math.floor(Math.random() * 100 + commit.author.length)
    totalLineAdditions += lines
  }
  return totalLineAdditions
}

const MemberSummary = ({ projectId, memberData }: IMemberSummaryProps) => {
  if (!memberData || !projectId) return null

  const { id, displayName } = memberData

  const { Suspense: DataSuspense, data, error } = useSuspense<
    IMemberStatData,
    Error
  >((setData, setError) => {
    let otherData: ICommitData[] | IMergeData[] | null = null
    jsonFetcher<IMergeData[]>(
      `/api/project/${projectId}/members/${id}/mergerequests`
    )
      .then(data => {
        if (otherData) {
          setData(computeStats(otherData as ICommitData[], data))
        } else {
          otherData = data
        }
      })
      .catch(onError(setError))
    jsonFetcher<ICommitData[]>(
      `/api/project/${projectId}/members/${displayName}/commits`
    )
      .then(data => {
        if (otherData) {
          setData(computeStats(data, otherData as IMergeData[]))
        } else {
          otherData = data
        }
      })
      .catch(onError(setError))
  })

  const memberStatData = [
    {
      name: 'Merge request score',
      value: computeMergeScore(data?.mergeRequests),
      description: 'Sum of merge request scores for selected date range',
    },
    {
      name: 'Commit score',
      value: computeCommitScore(data?.commits),
      description: 'Sum of commit scores for selected date range',
    },
    {
      name: 'Total merge requests',
      value: data?.mergeRequests.length,
      description: 'Number of merge requests made',
    },

    {
      name: 'Total commits',
      value: data?.commits.length,
      description: 'Number of commits made',
    },
    {
      name: 'Lines of code added',
      value: computeLinesAdded(data?.commits),
    },
  ]

  return (
    <div className={styles.container}>
      <div className={styles.statsContainer}>
        <div className={styles.graph}>
          <DataSuspense
            fallback={`Analyzing ${memberData?.displayName} . . . `}
            error={error?.message ?? 'Unknown Error'}
          >
            <ActivityGraph
              mergeUrl={`/api/project/${projectId}/members/${id}/mergerequests`}
              commitUrl={`/api/project/${projectId}/members/${displayName}/commits`}
              yAxisValue={'number'}
            />
          </DataSuspense>
        </div>
        <StatSummary statData={memberStatData} />
      </div>
    </div>
  )
}

export default MemberSummary
