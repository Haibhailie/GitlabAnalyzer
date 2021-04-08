import { IProjectData } from '../types'

import ActivityGraph from './ActivityGraph'

import styles from '../css/ProjectSummary.module.css'

const ProjectSummary = ({ project }: { project: IProjectData | undefined }) => {
  if (!project) return null

  const { id, name } = project

  return (
    <div className={styles.container}>
      <ActivityGraph
        mergeUrl={`/api/project/${id}/mergerequests`}
        commitUrl={`/api/project/${id}/commits`}
        graphTitle={`${name} Summary`}
      />
    </div>
  )
}

export default ProjectSummary
