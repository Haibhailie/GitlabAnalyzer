import { useContext } from 'react'
import { useHistory } from 'react-router-dom'
import withSuspense from '../utils/withSuspense'
import useSuspense from '../utils/useSuspense'
import jsonFetch from '../utils/jsonFetcher'

import { ProjectContext } from '../context/ProjectContext'
import Table from '../components/Table'
import Loading from '../components/Loading'
import ErrorComp from '../components/Error'

import styles from '../css/Home.module.css'

import gt from '../assets/greater-than.svg'

export type TProjects = {
  id: string
  name: string
  role: string
  latestUpdate: number
  analyzed: boolean
}[]

// const [Suspense, useContent] = withSuspense<TProjects, Error>(
//   (setData, setError) => {
//     jsonFetch<TProjects>('/api/projects')
//       .then(data => {
//         setData(data)
//       })
//       .catch(err => {
//         if (err.message === '401') {
//           window.location.href = '/'
//         } else if (err.message === 'Failed to fetch') {
//           setError(new Error('Could not connect to server'))
//         } else {
//           setError(new Error('Server error. Please try again.'))
//         }
//       })
//   }
// )

const Home = () => {
  const { Suspense, data, error } = useSuspense<TProjects, Error>(
    (setData, setError) => {
      // jsonFetch<TProjects>('/api/projects')
      //   .then(data => {
      //     setData(data)
      //   })
      //   .catch(err => {
      //     if (err.message === '401') {
      //       window.location.href = '/'
      //     } else if (err.message === 'Failed to fetch') {
      //       setError(new Error('Could not connect to server'))
      //     } else {
      //       setError(new Error('Server error. Please try again.'))
      //     }
      //   })
      setTimeout(() => {
        setData([
          {
            id: '1',
            name: '373-2021-1-Orcus / GitLabAnalyzer',
            role: 'Maintainer',
            latestUpdate: 1613622882116,
            analyzed: false,
          },
          {
            id: '2',
            name: 'React',
            role: 'Developer',
            latestUpdate: 1613622892116,
            analyzed: false,
          },
          {
            id: '3',
            name: 'Vue',
            role: 'Owner',
            latestUpdate: 1613622802116,
            analyzed: false,
          },
        ])
      }, 2000)
    }
  )

  // const { data, error } = useContent()
  const history = useHistory()
  const { dispatch } = useContext(ProjectContext)

  const onAnalyze = (id: string) => {
    dispatch({ type: 'SET_ID', id })
    history.push(`/project/${id}`)
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
            data={data?.map(({ id, name, analyzed, latestUpdate, role }) => {
              return {
                name,
                role,
                latestUpdate,
                analyzed: analyzed ? 'Yes' : 'No',
                action: (
                  <button
                    className={styles.analyze}
                    onClick={() => onAnalyze(id)}
                  >
                    Analyze
                    <img src={gt} />
                  </button>
                ),
              }
            })}
            headers={['Project Name', 'Role', 'Last Updated', 'Analyzed?', '']}
            classes={{
              table: styles.table,
              header: styles.theader,
              data: styles.data,
            }}
            columnWidths={['3fr', '2fr', '2fr', '1fr', '1fr']}
            sortable
          />
        )}
      </div>
    </Suspense>
  )
}

export default Home
