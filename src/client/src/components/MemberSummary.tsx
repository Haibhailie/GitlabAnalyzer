import { useState } from 'react'
import { IMemberData, IMemberStatData } from '../pages/Member'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

export interface IMemberSummaryProps {
  projectId: string
  memberData: IMemberData | undefined
  memberStats: IMemberStatData
}

const MemberSummary = ({
  projectId,
  memberData,
  memberStats,
}: IMemberSummaryProps) => {
  if (!memberData || !projectId) return null

  const { id, username, displayName, role } = memberData
  const {
    numCommits,
    commitScore,
    numMergeRequests,
    mergeRequestScore,
  } = memberStats

  console.log(displayName)

  const memberStatData = [
    {
      name: 'Merge request score',
      value: mergeRequestScore,
    },
    {
      name: 'Commit score',
      value: commitScore,
      description: 'Sum of commit scores for selected date range',
    },
    {
      name: 'Total merge requests',
      value: numMergeRequests,
      description: 'Number of merge requests made',
    },

    {
      name: 'Total commits',
      value: numCommits,
      description: 'Number of commits made',
    },
    {
      name: 'Lines of code',
      value: 399,
    },
  ]

  return (
    <div className={styles.container}>
      <div className={styles.statsContainer}>
        <div className={styles.graph}>
          <ActivityGraph
            mergeUrl={`/api/project/${projectId}/members/${id}/mergerequests`}
            commitUrl={`api/project/${projectId}/members/${displayName}/commits`}
            yAxisValue={'number'}
          />
        </div>
        <StatSummary statData={memberStatData} />
      </div>
    </div>
  )
}

export default MemberSummary
