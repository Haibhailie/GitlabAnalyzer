import { useState } from 'react'
import { IProjectData } from '../pages/Project'
import bytesConverter from '../utils/bytesConverter'

import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/ProjectSummary.module.css'
import StatSummary from './StatSummary'

const calcAgeInDays = (birth: number) => {
  const diff = Date.now() - birth
  return diff / (24 * 60 * 60 * 1000)
}

const ProjectSummary = ({ project }: { project: IProjectData | undefined }) => {
  const [yAxis, setYAxis] = useState<'number' | 'score'>('number')

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
      name: 'Files',
      rawValue: bytesConverter(repoSize).split(' ')[0],
      value: bytesConverter(repoSize),
      description: 'Total size of all files',
    },
  ]

  return (
    <div className={styles.container}>
      <div className={styles.statsContainer}>
        <div className={styles.graph}>
          <ActivityGraph
            mergeUrl={`/api/project/${id}/mergerequests`}
            commitUrl={`/api/project/${id}/commits`}
            yAxisValue={yAxis}
          />
        </div>
        <StatSummary statData={projectStatData} />
      </div>
    </div>
  )
}

export default ProjectSummary
