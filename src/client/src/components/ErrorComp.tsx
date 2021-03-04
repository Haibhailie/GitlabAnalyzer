import styles from '../css/Error.module.css'

import error from '../assets/error.svg'

export interface IErrorCompProps {
  message: string
}

const ErrorComp = ({ message }: IErrorCompProps) => {
  return (
    <div className={styles.container}>
      <img src={error} className={styles.error} />
      <p className={styles.message}>{message}</p>
    </div>
  )
}

export default ErrorComp
