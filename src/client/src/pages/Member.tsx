import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import {
  useHistory,
  useParams,
  RouteComponentProps,
  useLocation,
} from 'react-router-dom'
import { Location } from 'history'
import { onError } from '../utils/suspenseDefaults'
import IMember from '../pages/Project'

import Loading from '../components/Loading'
import ErrorComp from '../components/ErrorComp'
import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'

import styles from '../css/Member.module.css'

export interface IMemberProps {
  username: string
  displayName: string
  role: string
}

const Member = () => {
  const { id, memberId } = useParams<{ id: string; memberId: string }>()
  const location = useLocation()
  console.log(location.state)

  return (
    <div className={styles.container}>
      <h1>Member name</h1>
      <Selector tabHeaders={['Summary', 'Merge Requests', 'Comments']}>
        <div className={styles.summaryContainer}>
          <MemberSummary />
        </div>
        <div className={styles.mergeRequestsContainer}>
          <h1>Merge requests</h1>
        </div>
        <div className={styles.commentsContainer}>
          <h1>Comments Table</h1>
        </div>
      </Selector>
    </div>
  )
}

export default Member
