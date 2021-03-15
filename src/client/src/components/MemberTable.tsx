import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'
import { TMemberData } from '../types'

import Table from '../components/Table'
import AnalyzeButton from './AnalyzeButton'

import styles from '../css/MemberTable.module.css'

export interface IActivityGraphProps {
  projectId: string
  projectName?: string
}

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
      fallback="Loading Commit Data..."
      error={error?.message ?? 'Unknown Error'}
    >
      <Table
        sortable
        headers={['Member', 'Username', 'Role', '']}
        columnWidths={['2fr', '2fr', '2fr', '3fr']}
        classes={{
          container: styles.tableContainer,
          table: styles.table,
          header: styles.theader,
          data: styles.tdata,
        }}
        title={`Members of ${projectName}`}
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
    </Suspense>
  )
}

export default MemberTable
