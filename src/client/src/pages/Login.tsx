import styles from '../css/Login.module.css'
import astronaut from '../assets/splash.svg'
import LoginFields from '../components/LoginFields'

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

      {/* <button className={styles.loginButton}>Login</button> */}
      <div className={styles.loginContainer}>
        <LoginFields />
      </div>
    </div>
  )
}

export default Login
