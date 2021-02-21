import { useHistory } from 'react-router-dom'
import { useState } from 'react'

import styles from '../css/LoginFields.module.css'
import Selector from './Selector'

const LoginFields = () => {
  const history = useHistory()
  const [errorUserPass, setErrorUserPass] = useState('')
  const [errorPat, setErrorPat] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [pat, setPat] = useState('')

  const handleUserPassSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    fetch('/api/signin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username, password: password }),
      credentials: 'include',
    })
      .then(res => {
        if (res.status === 200) {
          history.push('/home')
        } else if (res.status === 400) {
          setErrorUserPass('Please fill in both fields')
        } else if (res.status === 401) {
          setErrorUserPass('Invalid credentials')
        } else if (res.status === 500) {
          setErrorUserPass('Could not connect to server')
        }
      })
      .catch(() => {
        setErrorUserPass('Could not connect to server')
      })
  }

  const handlePatSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    fetch('/api/auth', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pat: pat }),
      credentials: 'include',
    })
      .then(res => {
        if (res.status === 200) {
          history.push('/home')
        } else if (res.status === 400) {
          setErrorPat('Please fill in your token')
        } else if (res.status === 401) {
          setErrorPat('Invalid Personal Access Token')
        } else if (res.status === 500) {
          setErrorPat('Could not connect to server')
        }
      })
      .catch(() => {
        setErrorPat('Could not connect to server')
      })
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Login</h1>
      <Selector tabHeaders={['Username/Password', 'Personal Access Token']}>
        <form className={styles.fields} onSubmit={handleUserPassSubmit}>
          {errorUserPass && <p className={styles.error}>{errorUserPass}</p>}
          <label className={styles.fieldHeader}>Username</label>
          <input
            type="text"
            name="username"
            placeholder="Enter username"
            className={styles.field}
            onChange={event => setUsername(event.target.value)}
          />
          <label className={styles.fieldHeader}>Password</label>
          <input
            type="password"
            name="password"
            placeholder="Enter password"
            className={styles.field}
            onChange={event => setPassword(event.target.value)}
          />
          <input type="submit" value="Log In" className={styles.button} />
        </form>
        <form className={styles.fields} onSubmit={handlePatSubmit}>
          {errorPat && <p className={styles.error}>{errorPat}</p>}
          <label className={styles.fieldHeader}>
            GitLab Personal Access Token
          </label>
          <input
            type="text"
            name="pat"
            placeholder="Enter GitLab Personal Access Token"
            className={styles.field}
            onChange={event => setPat(event.target.value)}
          />
          <input type="submit" value="Log In" className={styles.button} />
        </form>
      </Selector>
    </div>
  )
}

export default LoginFields
