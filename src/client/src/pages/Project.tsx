import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { useParams } from 'react-router-dom'
import { onError } from '../utils/suspenseDefaults'
import { IProjectData } from '../types'

import Loading from '../components/Loading'
import ErrorComp from '../components/ErrorComp'
import Selector from '../components/Selector'
import MemberTable from '../components/MemberTable'
import ProjectSummary from '../components/ProjectSummary'

import styles from '../css/Project.module.css'
import useProject from '../utils/useProject'

export interface IMember {
  id: string
  username: string
  displayName: string
  role: string
}

const Project = () => {
  const { id } = useParams<{ id: string }>()

  const projectId = parseInt(id)

  const project = useProject(projectId)

  const { Suspense, data, error } = useSuspense<IProjectData>(
    (setData, setError) => {
      if (project === 'LOADING') {
        return
      } else if (project) {
        jsonFetcher<IProjectData>(`/api/project/${id}`)
          .then(data => {
            setData(data)
          })
          .catch(onError(setError))
      } else {
        setError(new Error(`Could not load project ${id}`))
      }
    },
    [project]
  )

  return (
    <Suspense
      fallback={<Loading message="Getting project details.." />}
      error={<ErrorComp message={error?.message ?? 'Unknown Error'} />}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>{data?.name}</h1>
        <Selector tabHeaders={['Summary', 'Members']}>
          <div className={styles.summaryContainer}>
            <ProjectSummary projectName={data?.name ?? ''} />
          </div>
          <div className={styles.memberContainer}>
            <MemberTable projectId={id} projectName={data?.name ?? ''} />
          </div>
        </Selector>
      </div>
    </Suspense>
  )
}

export default Project
