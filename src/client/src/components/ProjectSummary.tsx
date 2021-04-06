import { IProjectData } from '../types'
import bytesConverter from '../utils/bytesConverter'
import { round } from 'lodash'

import ActivityGraph from './ActivityGraph'
import StatSummary from './StatSummary'

import styles from '../css/ProjectSummary.module.css'

const calcAgeInDays = (birth: number) => {
  const diff = Date.now() - birth
  return diff / (24 * 60 * 60 * 1000)
}

const ProjectSummary = ({ project }: { project: IProjectData | undefined }) => {
  if (!project) return null

  const { id, members, numBranches, numCommits, createdAt, repoSize } = project

  const projectStatData = [
    {
      name: 'Members',
      value: members.length,
    },
    {
      name: 'Branches',
      value: numBranches,
    },
    {
      name: 'Total commits',
      value: numCommits,
    },
    {
      name: 'Average commits per day',
      value: round(numCommits / calcAgeInDays(createdAt), 2),
    },
    {
      name: 'Files',
      rawValue: round(repoSize / 1024, 2),
      value: bytesConverter(repoSize),
    },
  ]

  return (
    <div className={styles.container}>
      <div className={styles.statsContainer}>
        <div className={styles.graph}>
          <ActivityGraph
            mergeUrl={`/api/project/${id}/mergerequests`}
            commitUrl={`/api/project/${id}/commits`}
          />
        </div>
        <StatSummary statData={projectStatData} />
      </div>
    </div>
  )
}

export default ProjectSummary
