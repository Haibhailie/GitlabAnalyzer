import styles from '../css/Login.module.css'
import astronaut from '../assets/splash.svg'

const Login: React.FC = () => {
  return (
    <div className={styles.container}>
      <div className={styles.splash}>
        <img src={astronaut} alt="Astronaut holding flag" />
        <div className={styles.loginButtonDiv}>
          <button className={styles.loginButton}>Login</button>
        </div>
      </div>
    </div>
  )
}

export default Login
