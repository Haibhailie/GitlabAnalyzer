import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'

import Table from '../components/Table'
import AnalyzeButton from './AnalyzeButton'

import styles from '../css/MemberTable.module.css'

export interface IActivityGraphProps {
  projectId: string
  projectName?: string
}

export type TMemberData = {
  id: string
  username: string
  displayName: string
  role: string
}[]

const MemberTable = ({ projectId, projectName }: IActivityGraphProps) => {
  const history = useHistory()
  const { Suspense, data, error } = useSuspense<TMemberData, Error>(
    (setData, setError) => {
      jsonFetcher<TMemberData>(`/api/project/${projectId}/members`)
        .then(members => {
          setData(members)
        })
        .catch(onError(setError))
    }
  )

  const onAnalyze = (id: string) => {
    history.push(`/project/${projectId}/member/${id}`)
  }

  return (
    <Suspense
      fallback="Loading Members..."
      error={error?.message ?? 'Unknown Error'}
    >
      <>
        <div className={styles.header}>Members of {projectName}</div>
        <Table
          sortable
          headers={['Member', 'Username', 'Role', '']}
          columnWidths={['2fr', '2fr', '2fr', '3fr']}
          classes={{
            table: styles.table,
            header: styles.theader,
            data: styles.tdata,
          }}
          data={
            data?.map(({ displayName, id, role, username }) => {
              return {
                displayName,
                username,
                role,
                analyze: (
                  <AnalyzeButton
                    message={`Analyze ${`${displayName} `.split(' ')[0]}`}
                    id={id}
                    onClick={onAnalyze}
                    className={styles.analyze}
                  />
                ),
              }
            }) ?? [{}]
          }
        />
      </>
    </Suspense>
  )
}

export default MemberTable
