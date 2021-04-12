import useProject from '../utils/useProject'

import ActivityGraph from './ActivityGraph'

import styles from '../css/ProjectSummary.module.css'

export interface IProjectSummaryProps {
  projectName: string
}

const ProjectSummary = ({ projectName }: IProjectSummaryProps) => {
  const project = useProject()
  if (!project || project === 'LOADING') return null

  const { mergeRequests } = project

  return (
    <div className={styles.container}>
      <ActivityGraph
        mergeRequests={mergeRequests}
        graphTitle={`${projectName} Summary`}
      />
    </div>
  )
}

export default ProjectSummary
