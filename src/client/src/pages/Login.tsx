import styles from '../css/Login.module.css'
import astronaut from '../assets/splash.svg'

const Login = () => {
  return (
    <div className={styles.splash}>
      <img
        className={styles.splashImg}
        src={astronaut}
        alt="Astronaut holding flag"
      />
      <button className={styles.loginButton}>Login</button>
    </div>
  )
}

export default Login
