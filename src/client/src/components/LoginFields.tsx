import { useHistory } from 'react-router-dom'
import { useState } from 'react'

import styles from '../css/Login.module.css'

const LoginFields = () => {
  const history = useHistory()
  const [error, setError] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const handleUserPassSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    fetch('http://localhost:8080/api/signin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username, password: password }),
    })
      .then(res => {
        if (res.status === 200) {
          history.push('/home')
        } else if (res.status === 400) {
          setError('Please fill in both fields')
        } else if (res.status === 401) {
          setError('Invalid credentials')
        } else if (res.status === 500) {
          setError('Could not connect to server')
        }
      })
      .catch(error => {
        setError('Could not connect to server')
      })
  }

  const handlePatSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    console.log(username)
    fetch('http://localhost:8080/api/auth', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pat: username }),
    })
      .then(res => {
        if (res.status === 200) {
          history.push('/home')
        } else if (res.status === 400) {
          setError('Please fill in both fields')
        } else if (res.status === 401) {
          setError('Invalid Personal Access Token')
        } else if (res.status === 500) {
          setError('Could not connect to server')
        }
      })
      .catch(error => {
        setError('Could not connect to server')
      })
  }

  return (
    <div className={styles.loginFieldsContainer}>
      <h1 className={styles.loginTitle}>Login</h1>
      {/** TODO: ADD GENERIC SELECTOR */}
      <form className={styles.loginFields} onSubmit={handleUserPassSubmit}>
        {error && <p className={styles.loginError}>{error}</p>}
        <label className={styles.loginHeader}>Username</label>
        <input
          type="text"
          name="username"
          placeholder="Enter username"
          className={styles.loginField}
          onChange={event => setUsername(event.target.value)}
        />
        <label className={styles.loginHeader}>Password</label>
        <input
          type="password"
          name="password"
          placeholder="Enter password"
          className={styles.loginField}
          onChange={event => setPassword(event.target.value)}
        />
        <input type="submit" value="Log In" className={styles.loginButton} />
      </form>
      <form className={styles.loginFields} onSubmit={handlePatSubmit}>
        {error && <p className={styles.loginError}>{error}</p>}
        <label className={styles.loginHeader}>
          GitLab Personal Access Token
        </label>
        <input
          type="text"
          name="pat"
          placeholder="Enter GitLab Personal Access Token"
          className={styles.loginField}
          onChange={event => setUsername(event.target.value)}
        />
        <input type="submit" value="Log In" className={styles.loginButton} />
      </form>
    </div>
  )
}

export default LoginFields
