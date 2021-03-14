import { useLocation, useParams } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { ICommitData, IMergeData } from '../components/ActivityGraph'

import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'

import styles from '../css/Member.module.css'

export interface IMemberData {
  id: string
  username: string
  displayName: string
  role: string
}

export interface IMemberStatData {
  commits: ICommitData[]
  mergeRequests: IMergeData[]
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

  return (
    <MemberSuspense
      fallback="Getting member details..."
      error={memberError?.message ?? 'Unknown Error'}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>{memberData?.displayName}</h1>
        <h3 className={styles.subheader}>
          {memberData?.username && `@${memberData?.username}`}
        </h3>
        <Selector tabHeaders={['Summary', 'Merge Requests', 'Comments']}>
          <div className={styles.summaryContainer}>
            <MemberSummary projectId={id} memberData={memberData} />
          </div>
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
