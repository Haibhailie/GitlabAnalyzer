import { useParams } from 'react-router-dom'
import useSuspense from '../utils/useSuspense'
import { TMergeData, TCommitData } from '../types'

import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'
import MemberDropdown from '../components/MemberDropdown'
import MergeRequests from '../components/MergeRequests'
import CommentTable from '../components/CommentTable'

import styles from '../css/Member.module.css'
import useProject from '../utils/useProject'
import { IProjectState } from '../context/ProjectContext'

export interface IMemberStatData {
  commits: TCommitData
  mergeRequests: TMergeData
}

const Member = () => {
  const { id: projectIdStr, memberId: memberIdStr } = useParams<{
    id: string
    memberId: string
  }>()

  const projectId = parseInt(projectIdStr)
  const memberId = parseInt(memberIdStr)

  const project = useProject(projectId)

  const { Suspense, data, error: memberError } = useSuspense<IProjectState>(
    (setData, setError) => {
      if (!projectId || !memberId)
        return setError(new Error('Invalid project or member id.'))

      if (!project) {
        setError(new Error('Failed to load member data.'))
      } else if (project !== 'LOADING') {
        setData(project)
      }
    },
    [projectId, memberId, project]
  )

  const member = data?.members[memberId]

  return (
    <Suspense
      fallback="Getting member details..."
      error={memberError?.message ?? 'Unknown Error'}
    >
      <div className={styles.container}>
        <div className={styles.containerHeader}>
          <MemberDropdown
            members={Object.values(data?.members ?? {})}
            projectId={projectId}
            currentMemberId={memberId}
          />
        </div>
        <h1 className={styles.header}>{member?.displayName}</h1>
        <h3 className={styles.subheader}>
          {member?.username && `@${member?.username}`}
        </h3>
        <Selector tabHeaders={['Summary', 'Merge Requests', 'Comments']}>
          <div className={styles.summaryContainer}>
            <MemberSummary memberId={memberId} />
          </div>
          <div className={styles.mergeRequestsContainer}>
            <MergeRequests projectId={projectId} memberId={memberId} />
          </div>
          <div className={styles.commentsContainer}>
            <CommentTable comments={member?.notes ?? []} />
          </div>
        </Selector>
      </div>
    </Suspense>
  )
}

export default Member
