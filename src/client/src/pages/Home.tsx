import { ChangeEvent, useState } from 'react'
import { useHistory } from 'react-router-dom'
import useSuspense from '../utils/useSuspense'
import jsonFetch from '../utils/jsonFetcher'
import { NETWORK_ERROR, SERVER_ERROR } from '../utils/constants'
import dateConverter from '../utils/dateConverter'

import Table from '../components/Table'
import Loading from '../components/Loading'
import ErrorComp from '../components/ErrorComp'
import AnalyzeButton from '../components/AnalyzeButton'
import SearchBar from '../components/SearchBar'

import styles from '../css/Home.module.css'

import { ReactComponent as reload } from '../assets/reload.svg'
import { ReactComponent as errorSmall } from '../assets/error-small.svg'

export type TProjects = {
  id: string
  name: string
  role: string
  lastActivityTime: number
  lastAnalysisTime: number
}[]

const Home = () => {
  const history = useHistory()
  const [analysis, setAnalysis] = useState<
    Record<string, { isAnalyzing?: boolean; analyzeError?: boolean }>
  >({})
  const [filteredData, setFilteredData] = useState<TProjects>([])

  const { Suspense, data, error } = useSuspense<TProjects, Error>(
    (setData, setError) => {
      jsonFetch<TProjects>('/api/projects')
        .then(data => {
          setData(data)
          setFilteredData(data)
          data.forEach(({ id }) => {
            analysis[id] = {
              isAnalyzing: false,
              analyzeError: false,
            }
          })
          setAnalysis({ ...analysis })
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
  const onAnalyze = (id: string | number) => {
    history.push(`/project/${id}`)
  }

  const updateAnalyzing = (id: string, value: boolean) => {
    analysis[id].isAnalyzing = value
    setAnalysis({ ...analysis })
  }

  const updateAnalyzingError = (id: string, value: boolean) => {
    analysis[id].analyzeError = value
    setAnalysis({ ...analysis })
  }

  const preAnalyze = (id: string | number) => {
    id = id.toString()

    updateAnalyzingError(id, false)
    updateAnalyzing(id, true)

    jsonFetch(`/api/${id}/analyze`, {
      responseIsEmpty: true,
      method: 'PUT',
    })
      .then(res => {
        id = id.toString()
        analysis[id].isAnalyzing = false
        setAnalysis({ ...analysis })
        if (res === 200) {
          // TODO: update last analyzed
          setFilteredData(
            filteredData.map(project => {
              if (project.id === id) {
                project.lastAnalysisTime = Date.now()
              }
              return { ...project }
            })
          )
        } else if (res === 401 || res === 400) {
          history.push('/login')
        } else {
          updateAnalyzingError(id, true)
        }
      })
      .catch(() => {
        id = id.toString()
        updateAnalyzing(id, false)
        updateAnalyzingError(id, true)
      })
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
              ({ id, name, lastAnalysisTime, lastActivityTime, role }) => {
                return {
                  name,
                  role,
                  lastActivityTime: dateConverter(lastActivityTime, true),
                  lastAnalyzedAt: lastAnalysisTime
                    ? dateConverter(lastAnalysisTime, true)
                    : 'N/A',
                  action: (
                    <AnalyzeButton
                      id={id}
                      onClick={preAnalyze}
                      message={lastAnalysisTime ? 'Re-Analyze' : 'Pre-Analyze'}
                      disabled={
                        lastActivityTime <= lastAnalysisTime ||
                        analysis[id]?.isAnalyzing
                      }
                      isAnalyzing={analysis[id]?.isAnalyzing}
                      Icon={analysis[id]?.analyzeError ? errorSmall : reload}
                    />
                  ),
                }
              }
            )}
            headers={[
              'Project Name',
              'Role',
              'Last GitLab Activity',
              'Last Analyzed',
              '',
            ]}
            classes={{
              container: styles.tableContainer,
              table: styles.table,
              header: styles.theader,
              data: styles.data,
            }}
            columnWidths={['3fr', '2fr', '2fr', '2fr', '2fr']}
            sortable
            onClick={(e, index) => {
              onAnalyze(filteredData[index].id)
            }}
          />
        )}
        {filteredData.length === 0 && <ErrorComp message="No Results Found" />}
      </div>
    </Suspense>
  )
}

export default Home
