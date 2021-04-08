import { ChangeEvent, useContext, useState } from 'react'
import { useHistory } from 'react-router-dom'
import useSuspense from '../utils/useSuspense'
import jsonFetch from '../utils/jsonFetcher'
import { NETWORK_ERROR, SERVER_ERROR } from '../utils/constants'
import dateConverter from '../utils/dateConverter'
import { ProjectContext } from '../context/ProjectContext'

import Table from '../components/Table'
import Loading from '../components/Loading'
import ErrorComp from '../components/ErrorComp'
import AnalyzeButton from '../components/AnalyzeButton'
import SearchBar from '../components/SearchBar'

import styles from '../css/Home.module.css'

export type TProjects = {
  id: string
  name: string
  role: string
  lastActivityAt: number
  analyzed: boolean
}[]

const Home = () => {
  const history = useHistory()
  const { dispatch } = useContext(ProjectContext)
  const [filteredData, setFilteredData] = useState<TProjects>([])
  const { Suspense, data, error } = useSuspense<TProjects, Error>(
    (setData, setError) => {
      jsonFetch<TProjects>('/api/projects')
        .then(data => {
          setData(data)
          setFilteredData(data)
        })
        .catch(err => {
          if (err.message === '401' || err.message === '400') {
            history.push('/login')
          } else if (err.message === 'Failed to fetch') {
            setError(new Error(NETWORK_ERROR))
          } else {
            setError(new Error(SERVER_ERROR))
          }
        })
    }
  )

  const onAnalyze = (id: string) => {
    dispatch({ type: 'SET_ID', id })
    history.push(`/project/${id}`)
  }

  const onSearch = (event: ChangeEvent<HTMLInputElement>) => {
    const input = event.target.value.toLowerCase()
    if (input && data) {
      setFilteredData(
        data.filter(data => data.name.toLowerCase().includes(input))
      )
    } else {
      data ? setFilteredData([...data]) : setFilteredData([])
    }
  }

  return (
    <Suspense
      fallback={<Loading message="Loading Projects..." />}
      error={<ErrorComp message={error?.message ?? 'Unknown Error'} />}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>Your Projects</h1>
        <SearchBar placeholder="Search projects..." onSearch={onSearch} />
        {filteredData && (
          <Table
            data={filteredData?.map(
              ({ id, name, analyzed, lastActivityAt, role }) => {
                return {
                  name,
                  role,
                  lastActivityAt: dateConverter(lastActivityAt, true),
                  analyzed: analyzed ? 'Yes' : 'No',
                  action: (
                    <AnalyzeButton
                      id={id}
                      onClick={onAnalyze}
                      message="Analyze"
                    />
                  ),
                }
              }
            )}
            headers={['Project Name', 'Role', 'Last Updated', 'Analyzed?', '']}
            classes={{
              container: styles.tableContainer,
              table: styles.table,
              header: styles.theader,
              data: styles.data,
            }}
            columnWidths={['3fr', '2fr', '2fr', '1fr', '1fr']}
            sortable
          />
        )}
        {filteredData.length === 0 && <ErrorComp message="No Results Found" />}
      </div>
    </Suspense>
  )
}

export default Home
