import { useContext, useState } from 'react'
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

import styles from '../css/Home.module.css'

import { ReactComponent as reload } from '../assets/reload.svg'
import { ReactComponent as errorSmall } from '../assets/error-small.svg'

export type TProjects = {
  id: string
  name: string
  role: string
  lastActivityAt: number
  lastAnalyzedAt: number
}[]

const Home = () => {
  const history = useHistory()
  const { dispatch } = useContext(ProjectContext)

  const [isAnalyzing, setIsAnalyzing] = useState<boolean[]>([])
  const [analyzeError, setAnalyzeError] = useState<boolean[]>([])

  const { Suspense, data, error } = useSuspense<TProjects, Error>(
    (setData, setError) => {
      jsonFetch<TProjects>('/api/projects')
        .then(data => {
          setData(data)
          const initialAnalyzing: boolean[] = []
          data.forEach(() => {
            initialAnalyzing.push(false)
          })
          setIsAnalyzing(initialAnalyzing)
          setAnalyzeError(initialAnalyzing)
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

  const updateAnalyzing = (index: number, value: boolean) => {
    const currentAnalyzing: boolean[] = isAnalyzing
    currentAnalyzing[index] = value
    setIsAnalyzing([...currentAnalyzing])
  }

  const updateAnalyzingError = (index: number, value: boolean) => {
    const currentAnalyzeError: boolean[] = analyzeError
    currentAnalyzeError[index] = value
    setAnalyzeError([...currentAnalyzeError])
  }

  const preAnalyze = (id: string, index: number) => {
    updateAnalyzingError(index, false)
    updateAnalyzing(index, true)

    jsonFetch(`/api/project/${id}/analyze`, {
      responseIsEmpty: true,
      method: 'PUT',
    })
      .then(res => {
        const currentAnalyzing: boolean[] = isAnalyzing
        currentAnalyzing[index] = false
        setIsAnalyzing([...currentAnalyzing])
        if (res === 200) {
          // TODO: update last analyzed
        } else if (res === 401 || res === 400) {
          history.push('/login')
        } else {
          updateAnalyzingError(index, true)
        }
      })
      .catch(() => {
        updateAnalyzing(index, false)
        updateAnalyzingError(index, true)
      })
  }

  return (
    <Suspense
      fallback={<Loading message="Loading Projects..." />}
      error={<ErrorComp message={error?.message ?? 'Unknown Error'} />}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>Your Projects</h1>
        {data && (
          <Table
            data={data?.map(
              ({ id, name, lastAnalyzedAt, lastActivityAt, role }, index) => {
                return {
                  name,
                  role,
                  lastActivityAt: dateConverter(lastActivityAt, true),
                  lastAnalyzedAt: lastAnalyzedAt
                    ? dateConverter(lastAnalyzedAt, true)
                    : 'N/A',
                  action: (
                    <AnalyzeButton
                      id={id}
                      index={index}
                      onClick={preAnalyze}
                      message={lastAnalyzedAt ? 'Re-Analyze' : 'Pre-Analyze'}
                      disabled={
                        lastActivityAt <= lastAnalyzedAt || isAnalyzing[index]
                      }
                      isAnalyzing={isAnalyzing[index]}
                      Icon={analyzeError[index] ? errorSmall : reload}
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
              onAnalyze(data[index].id)
            }}
          />
        )}
      </div>
    </Suspense>
  )
}

export default Home
