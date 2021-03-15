import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { IMemberStatData } from '../pages/Member'
import { IMemberData, ICommitData, IMergeData } from '../types'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

export interface IMemberSummaryProps {
  projectId: string
  memberData?: IMemberData
}

const computeStats = (
  commitData: ICommitData[],
  mergeData: IMergeData[]
): IMemberStatData => {
  return {
    commits: commitData,
    mergeRequests: mergeData,
  }
}

const computeCommitScore = (commitData: ICommitData[] = []): number =>
  commitData.reduce((accum, commit) => accum + commit.score, 0)

const computeMergeScore = (mergeRequestData: IMergeData[] = []): number =>
  mergeRequestData.reduce(
    (accum, mergeRequest) => accum + mergeRequest.score,
    0
  )

const computeLinesAdded = (commitData: ICommitData[] = []): number =>
  commitData.reduce(
    (accum, commit) =>
      accum + Math.floor(Math.random() * 80 + commit.author.length),
    0
  )

const MemberSummary = ({ projectId, memberData }: IMemberSummaryProps) => {
  const { Suspense, data, error } = useSuspense<IMemberStatData>(
    (setData, setError) => {
      let otherData: ICommitData[] | IMergeData[] | null = null

      if (projectId && memberData) {
        jsonFetcher<IMergeData[]>(
          `/api/project/${projectId}/members/${memberData.id}/mergerequests`
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
          `/api/project/${projectId}/members/${memberData.displayName}/commits`
        )
          .then(data => {
            if (otherData) {
              setData(computeStats(data, otherData as IMergeData[]))
            } else {
              otherData = data
            }
          })
          .catch(onError(setError))
      } else {
        setError(new Error('Cannot find member.'))
      }
    },
    [projectId, memberData]
  )

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
      <div className={styles.graph}>
        <Suspense
          fallback={`Analyzing ${memberData?.displayName} . . . `}
          error={error?.message ?? 'Unknown Error'}
        >
          <ActivityGraph
            mergeUrl={`/api/project/${projectId}/members/${memberData?.id}/mergerequests`}
            commitUrl={`/api/project/${projectId}/members/${memberData?.displayName}/commits`}
          />
        </Suspense>
      </div>
      <StatSummary statData={memberStatData} />
    </div>
  )
}

export default MemberSummary
