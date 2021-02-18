import { useEffect, useState } from 'react'
import { useHistory } from 'react-router-dom'
import withSuspense from '../utils/withSuspense'
import jsonFetch from '../utils/jsonFetcher'

import Table from '../components/Table'
import Loading from '../components/Loading'
import Error from '../components/Error'

import styles from '../css/Home.module.css'

interface IProjectsAPI {
  id: string
  name: string
  role: string
  latestUpdate: number
  analyzed: boolean
}

type TProjects = IProjectsAPI[]

const [Suspense, useContent] = withSuspense<TProjects, number | Error>(
  (setData, setError) => {
    jsonFetch<TProjects>('/api/projects')
      .then(data => {
        setData(data)
      })
      .catch(errCode => {
        setError(errCode)
      })
  }
)

const LoadedHome = () => {
  const history = useHistory()
  const { error, data } = useContent()

  if (error === 401) {
    document.cookie = 'sessionId=;expires=Thu, 01 Jan 1970 00:00:01 GMT'
    history.push('/')
  }

  return (
    <div>
      {data?.map?.(d => (
        <p key={d.id}>{d.name}</p>
      ))}
    </div>
  )
}

const Home = () => {
  return (
    <Suspense
      fallback={<Loading message="Loading Projects..." />}
      error={<Error message="Unable to access network" />}
    >
      <LoadedHome />
    </Suspense>
  )
}

export default Home
