import { useParams } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { IMemberData, TMemberData } from '../types'

import Table from '../components/Table'
import MemberDropdown from '../components/MemberDropdown'
import Button from '../components/Button'

import styles from '../css/MemberResolution.module.css'
import { identity } from 'lodash'

export interface ICommitterData {
  email: string
  name: string
  memberDto: IMemberData
}

const MemberResolution = () => {
  const { id } = useParams<{ id: string }>()
  const testCommitterData = [
    {
      name: 'Ali Khamesy',
      email: 'akhamesy@sfu.ca',
      memberDto: {
        id: '7',
        username: 'akhamesy',
        displayName: 'Ali Khamesy',
        role: 'MAINTAINER',
      },
    },
    {
      name: 'Grace Luo',
      email: 'grace.r.luo@gmail.com',
      memberDto: {
        id: '5',
        username: 'gracelu0',
        displayName: 'Grace Luo',
        role: 'MAINTAINER',
      },
    },
    {
      name: 'Grace Luo',
      email: 'gla74@sfu.ca',
      memberDto: {
        id: '15',
        username: 'gla74',
        displayName: 'Grace Luo',
        role: 'MAINTAINER',
      },
    },
  ]

  const testMemberData = [{ committer: 'akhamesy', member: 'Ali Khamesy' }]

  const {
    Suspense: MemberSuspense,
    data: memberData,
    error: memberError,
  } = useSuspense<TMemberData, Error>((setData, setError) => {
    jsonFetcher<TMemberData>(`/api/project/${id}/members`)
      .then(members => {
        setData(members)
      })
      .catch(onError(setError))
  })

  //   const {
  //     Suspense: CommitterSuspense,
  //     data: committerData,
  //     error: committerError,
  //   } = useSuspense<ICommitterData[], Error>((setData, setError) => {
  //     jsonFetcher<ICommitterData[]>(`/api/project/${id}/committers`)
  //       .then(committers => {
  //         setData(committers)
  //       })
  //       .catch(onError(setError))
  //   })

  return (
    <MemberSuspense
      fallback="Getting project members..."
      error={memberError?.message ?? 'Unknown Error'}
    >
      {/* <CommitterSuspense
        fallback="Getting project committers..."
        error={committerError?.message ?? 'Unknown Error'}
      > */}
      <div className={styles.container}>
        <h1 className={styles.header}>Member to Committer Resolution</h1>
        <form className={styles.formContainer}>
          <Table
            headers={['Committer', 'Email', 'Member']}
            columnWidths={['2fr', '2fr', '3fr']}
            classes={{
              container: styles.tableContainer,
              table: styles.table,
              header: styles.theader,
              data: styles.tdata,
            }}
            data={
              testCommitterData?.map(({ name, email, memberDto }) => {
                return {
                  name,
                  email,
                  memberDto: memberData ? (
                    <MemberDropdown data={memberData} selected={memberDto.id} />
                  ) : null,
                }
              }) ?? [{}]
            }
          />
        </form>
        <button type="button" className={styles.button}>
          Save
        </button>
      </div>
      {/* </CommitterSuspense> */}
    </MemberSuspense>
  )
}

export default MemberResolution
