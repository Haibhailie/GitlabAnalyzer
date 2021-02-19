import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { useParams } from 'react-router-dom'

import Loading from '../components/Loading'
import ErrorComp from '../components/Error'

import styles from '../css/Project.module.css'
import ProjectStat from '../components/ProjectStat'

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

const calcAgeInDays = (birth: number) => {
  const diff = Date.now() - birth
  return diff / (24 * 60 * 60 * 1000)
}

const bytesToMb = (bytes: number) => bytes / (1024 * 1024)

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
        <div className={styles.main}>
          <div className={styles.graph}></div>
          {project && (
            <div className={styles.stats}>
              <ProjectStat name="Members" value={project.members.length} />
              <ProjectStat name="Branches" value={project.branches} />
              <ProjectStat name="Commits" value={project.commits} />
              <ProjectStat
                name="Average commits per day"
                value={(
                  project.commits / calcAgeInDays(project.createdAt)
                ).toPrecision(2)}
              />
              <ProjectStat
                name="Files"
                value={bytesToMb(project.repoSize).toPrecision(2)}
              />
            </div>
          )}
        </div>
      </div>
    </Suspense>
  )
}

export default Project
