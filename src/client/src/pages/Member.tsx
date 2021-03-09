import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { useParams } from 'react-router-dom'
import { onError } from '../utils/suspenseDefaults'

import Loading from '../components/Loading'
import ErrorComp from '../components/ErrorComp'
import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'

import styles from '../css/Member.module.css'

const Member = () => {
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
