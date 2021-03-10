import { useState } from 'react'

import StatSummary from '../components/StatSummary'
import ActivityGraph from '../components/ActivityGraph'

import styles from '../css/MemberSummary.module.css'

const MemberSummary = () => {
  const memberData = [
    {
      name: 'Merge request score',
      value: 25.5,
    },
    {
      name: 'Total commit score',
      value: 124,
    },
    {
      name: 'Lines of code',
      value: 399,
    },
    {
      name: 'Branches',
      value: 3,
    },
  ]

  return (
    <div className={styles.container}>
      <div className={styles.graph}>
        <ActivityGraph
          mergeUrl={`/api/project/5/mergerequests`}
          commitUrl={`/api/project/5/commits`}
        />
      </div>
      <StatSummary statData={memberData} />
    </div>
  )
}

export default MemberSummary
