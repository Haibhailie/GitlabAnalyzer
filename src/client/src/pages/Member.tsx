import { useLocation, useParams } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'

import {
  ICommitData,
  IMergeData,
  IActivityData,
} from '../components/ActivityGraph'

import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'

import styles from '../css/Member.module.css'

export interface IMemberData {
  id: string | number
  username: string
  displayName: string
  role: string
}

export interface IMemberStatData {
  numCommits: number | undefined
  commitScore: number
  numMergeRequests: number
  mergeRequestScore: number
}

const computeStats = (
  commitData: ICommitData[],
  mergeData: IMergeData[]
): IMemberStatData => {
  return {
    numCommits: commitData.length,
    numMergeRequests: mergeData.length,
    commitScore: 242,
    mergeRequestScore: 166,
  } as IMemberStatData
}

const Member = () => {
  const { id, memberId } = useParams<{ id: string; memberId: string }>()
  const { state } = useLocation<IMemberData>()

  const {
    Suspense: MemberSuspense,
    data: memberData,
    error: memberError,
  } = useSuspense<IMemberData, Error>((setData, setError) => {
    if (state) {
      setData(state)
    } else {
      jsonFetcher<IMemberData[]>(`/api/project/${id}/members`)
        .then(memberData => {
          for (const member of memberData) {
            if (member.id == memberId) {
              return setData(member)
            }
          }
        })
        .catch(onError(setError))
    }
  })

  const { Suspense: DataSuspense, data, error } = useSuspense<
    IMemberStatData,
    Error
  >((setData, setError) => {
    let otherData: ICommitData[] | IMergeData[] | null = null
    jsonFetcher<IMergeData[]>(
      `/api/project/${id}/members/${memberId}/mergerequests`
    )
      .then(data => {
        console.log(data)
        if (otherData) {
          setData(computeStats(otherData as ICommitData[], data))
        } else {
          otherData = data
        }
      })
      .catch(onError(setError))
    jsonFetcher<ICommitData[]>(
      `/api/project/${id}/members/${memberData?.displayName}/commits`
    )
      .then(data => {
        console.log(data)
        if (otherData) {
          setData(computeStats(data, otherData as IMergeData[]))
        } else {
          otherData = data
        }
      })
      .catch(onError(setError))
  })

  return (
    <MemberSuspense
      fallback="Getting member details..."
      error={memberError?.message ?? 'Unknown Error'}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>{memberData?.displayName}</h1>
        <Selector tabHeaders={['Summary', 'Merge Requests', 'Comments']}>
          <DataSuspense
            fallback={`Analyzing ${memberData?.displayName} . . `}
            error={error?.message ?? 'Unknown Error'}
          >
            <div className={styles.summaryContainer}>
              <MemberSummary
                projectId={id}
                memberData={memberData}
                memberStats={data}
              />
            </div>
          </DataSuspense>
          <div className={styles.mergeRequestsContainer}>
            <h1>Merge requests</h1>
          </div>
          <div className={styles.commentsContainer}>
            <h1>Comments Table</h1>
          </div>
        </Selector>
      </div>
    </MemberSuspense>
  )
}

export default Member
