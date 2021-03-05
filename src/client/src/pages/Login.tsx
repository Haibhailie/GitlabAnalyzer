import { useHistory } from 'react-router-dom'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import jsonFetcher from '../utils/jsonFetcher'

import LoginFields from '../components/LoginFields'

import styles from '../css/Login.module.css'

import astronaut from '../assets/splash.svg'

const Login = () => {
  const history = useHistory()
  const { Suspense, error } = useSuspense<null, Error>((setData, setError) => {
    jsonFetcher('/api/ping', {
      credentials: 'include',
      responseIsEmpty: true,
    })
      .then(resCode => {
        if (resCode === 200) {
          history.push('/home')
        } else if (resCode === 500) {
          setError(new Error('Server error. Please try again.'))
        } else {
          setData(null)
        }
      })
      .catch(onError(setError))
  })

  return (
    <Suspense fallback="" error={error?.message}>
      <div className={styles.splash}>
        <div className={styles.logoContainer}>
          <img
            className={styles.splashImg}
            src={astronaut}
            alt="Astronaut holding flag"
          />
        </div>
        <div className={styles.loginContainer}>
          <LoginFields />
        </div>
      </div>
    </Suspense>
  )
}

export default Login
