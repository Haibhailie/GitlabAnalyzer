import { useParams } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { IMemberData, ICommitData, IMergeData } from '../types'

import Table from '../components/Table'
import MemberDropdown from '../components/MemberDropdown'
import Button from '../components/Button'

import styles from '../css/MemberResolution.module.css'

export interface IMemberStatData {
  commits: ICommitData[]
  mergeRequests: IMergeData[]
}

const MemberResolution = () => {
  const { id } = useParams<{ id: string }>()

  const testData = [
    { committer: 'akhamesy', email: 'akhamesy@sfu.ca', member: 'Ali Khamesy' },
    { committer: 'gla74', email: 'gla74@sfu.ca', member: 'Grace Luo' },
  ]

  const memberData = [{ committer: 'akhamesy', member: 'Ali Khamesy' }]
  //   const { state } = useLocation<IMemberData>()

  //   const {
  //     Suspense,
  //     data: memberData,
  //     error: memberError,
  //   } = useSuspense<IMemberData>((setData, setError) => {
  //     if (state) {
  //       setData(state)
  //     } else {
  //       jsonFetcher<IMemberData[]>(`/api/project/${id}/members`)
  //         .then(memberData => {
  //           for (const member of memberData) {
  //             if (member.id == memberId) {
  //               return setData(member)
  //             }
  //           }
  //         })
  //         .catch(onError(setError))
  //     }
  //   })

  return (
    // <Suspense
    //   fallback="Getting member details..."
    //   error={memberError?.message ?? 'Unknown Error'}
    // >
    <div className={styles.container}>
      <h1 className={styles.header}>Member to Committer Resolution</h1>
      <form className={styles.formContainer}>
        <Table
          sortable
          headers={['Committer', 'Email', 'Member']}
          columnWidths={['2fr', '2fr', '3fr']}
          classes={{
            container: styles.tableContainer,
            table: styles.table,
            header: styles.theader,
            data: styles.tdata,
          }}
          data={
            testData?.map(({ committer, email, member }) => {
              return {
                committer,
                email,
                member: <MemberDropdown data={memberData} selected={member} />,
              }
            }) ?? [{}]
          }
        />
      </form>
      <button type="button" className={styles.button}>
        Save
      </button>
    </div>
    // </Suspense>
  )
}

export default MemberResolution
