import { useState } from 'react'
import { IMemberData } from '../pages/Member'
import { TMemberData } from '../components/MemberTable'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

export interface IMemberSummaryProps {
  projectId: string
  member: IMemberData | undefined
}

const MemberSummary = ({ projectId, member }: IMemberSummaryProps) => {
  if (!member || !projectId) return null

  const { id, username, displayName, role } = member

  const memberData = [
    {
      name: 'Merge request score',
      value: 25.5,
    },
    {
      name: 'Commit score',
      value: 124,
      description: 'Sum of commits scores for selected date range',
    },
    {
      name: 'Total commits',
      value: 3,
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
            commitUrl={`api/project/${projectId}/members/${username}/commits`}
            yAxisValue={'number'}
          />
        </div>
        <StatSummary statData={memberData} />
      </div>
    </div>
  )
}

export default MemberSummary
