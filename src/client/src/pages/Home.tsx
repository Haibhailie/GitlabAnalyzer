import { useHistory } from 'react-router-dom'
import withSuspense from '../utils/withSuspense'
import jsonFetch from '../utils/jsonFetcher'

import Table from '../components/Table'
import Loading from '../components/Loading'
import ErrorComp from '../components/Error'

import styles from '../css/Home.module.css'

export type TProjects = {
  id: string
  name: string
  role: string
  latestUpdate: number
  analyzed: boolean
}[]

const [Suspense, useContent] = withSuspense<TProjects, Error>(
  (setData, setError) => {
    jsonFetch<TProjects>('/api/projects')
      .then(data => {
        setData(data)
      })
      .catch(err => {
        if (err.message === '400') {
          setError(new Error('Not logged in'))
        } else if (err.message === 'Failed to fetch') {
          setError(new Error('Could not connect to server'))
        }
      })
  }
)

const Home = () => {
  const { data, error } = useContent()
  return (
    <Suspense
      fallback={<Loading message="Loading Projects..." />}
      error={<ErrorComp message={error?.message} />}
    >
      <div className={styles.container}>
        <h1 className={styles.header}>Your Projects</h1>
        <Table
          data={data?.map(row => {
            return {
              ...row,
              '': <button onClick={console.log}>Analyze</button>,
            }
          })}
          headers={['Project Name', 'Role', 'Last Updated', 'Analyzed?']}
          classes={{
            data: styles.data,
            header: styles.header,
            table: styles.table,
          }}
        />
      </div>
    </Suspense>
  )
}

export default Home
