import { useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'

import { onError } from '../utils/suspenseDefaults'
import { TMemberData } from '../components/MemberTable'

import Selector from '../components/Selector'
import MemberSummary from '../components/MemberSummary'

import styles from '../css/Member.module.css'

export interface IMemberData {
  id: string | number
  username: string
  displayName: string
  role: string
}

const Member = () => {
  const { id, memberId } = useParams<{ id: string; memberId: string }>()
  const { state } = useLocation<IMemberData[]>()

  const { Suspense, data, error } = useSuspense<IMemberData[], Error>(
    (setData, setError) => {
      if (state) {
        setData(state)
      } else {
        jsonFetcher<IMemberData[]>(`/api/project/${id}/members`)
          .then(members => {
            for (member of members) {
              if (member.id === memberId) return setData(member)
            }
          })
          .catch(onError(setError))
      }
    }
  )

  const testMember: IMemberData = {
    displayName: 'Dummy User',
    id: 14,
    role: 'MAINTAINER',
    username: 'dummyUsername',
  }

  return (
    <Suspense
      fallback="Analyzing member..."
      error={error?.message ?? 'Unknown Error'}
    >
      <div className={styles.container}>
        <h1>{data?.displayName}</h1>
        <Selector tabHeaders={['Summary', 'Merge Requests', 'Comments']}>
          <div className={styles.summaryContainer}>
            <MemberSummary projectId={id} member={testMember} />
          </div>
          <div className={styles.mergeRequestsContainer}>
            <h1>Merge requests</h1>
          </div>
          <div className={styles.commentsContainer}>
            <h1>Comments Table</h1>
          </div>
        </Selector>
      </div>
    </Suspense>
  )
}

export default Member
