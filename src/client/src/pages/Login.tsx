import LoginFields from '../components/LoginFields'

import styles from '../css/Login.module.css'

import astronaut from '../assets/splash.svg'

const Login = () => {
  return (
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
  )
}

export default Login
