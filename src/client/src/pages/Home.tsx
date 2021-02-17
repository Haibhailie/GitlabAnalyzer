import { useEffect, useState } from 'react'
import jsonFetch from '../utils/jsonFetcher'

import Table from '../components/Table'

import styles from '../css/Home.module.css'

const Home = () => {
  const [projects, setProjects] = useState([])

  useEffect(() => {
    jsonFetch('/api/')
  }, [])

  return <div className={styles.container}></div>
}

export default Home
