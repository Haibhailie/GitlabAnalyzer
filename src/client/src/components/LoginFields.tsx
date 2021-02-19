import styles from '../css/Login.module.css'
function handleSubmit(event: any) {
}

const LoginFields = () => {
  return (
    <div className={styles.loginFieldsContainer}>
      <h1 className={styles.loginTitle}>Login</h1>
      {/** TODO: ADD GENERIC SELECTOR */}
      <form className={styles.loginFields} onSubmit={handleSubmit}>
        <label className={styles.loginHeader}>Username</label>
        <input
          type="text"
          name="username"
          placeholder="Enter username"
          className={styles.loginField}
        ></input>
        <label className={styles.loginHeader}>Password</label>
        <input
          type="password"
          name="password"
          placeholder="Enter password"
          className={styles.loginField}
        ></input>
        <input
          type="submit"
          value="Log In"
          className={styles.loginButton}
        ></input>
      </form>
      <form className={styles.loginFields}>
        <label className={styles.loginHeader}>
          GitLab Personal Access Token
        </label>
        <input
          type="text"
          name="pat"
          placeholder="Enter GitLab Personal Access Token"
          className={styles.loginField}
        ></input>
        <input
          type="submit"
          value="Log In"
          className={styles.loginButton}
        ></input>
      </form>
    </div>
  )
}

export default LoginFields
