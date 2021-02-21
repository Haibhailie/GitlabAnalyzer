import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { useParams } from 'react-router-dom'

import Loading from '../components/Loading'
import ErrorComp from '../components/Error'
import Selector from '../components/Selector'
import MemberTable from '../components/MemberTable'
import ProjectSummary from '../components/ProjectSummary'

import styles from '../css/Project.module.css'

export interface IMember {
  id: string
  username: string
  displayName: string
  role: string
}

export interface IProjectData {
  id: string
  name: string
  members: IMember[]
  branches: number
  commits: number
  repoSize: number
  createdAt: number
}

const Project = () => {
  const { id } = useParams<{ id: string }>()

  const { Suspense, data: project, error } = useSuspense<IProjectData, Error>(
    (setData, setError) => {
      jsonFetcher<IProjectData>(`/api/project/${id}`)
        .then(data => setData(data))
        .catch(err => {
          if (err.message === '401') {
            window.location.href = '/'
          } else if (err.message === 'Failed to fetch') {
            setError(new Error('Could not connect to server'))
          } else {
            setError(new Error('Server error. Please try again.'))
          }
        })
    }
  )

  return (
    <Suspense
      fallback={<Loading message="Getting project details.." />}
      error={<ErrorComp message={error?.message ?? 'Unknown Error'} />}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>{project?.name}</h1>
        <Selector tabHeaders={['Summary', 'Members']}>
          <div className={styles.summaryContainer}>
            <ProjectSummary project={project} />
          </div>
          <div className={styles.memberContainer}>
            <MemberTable projectId={id} projectName={project?.name ?? ''} />
          </div>
        </Selector>
      </div>
    </Suspense>
  )
}

export default Project
