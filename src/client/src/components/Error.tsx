import styles from '../css/Error.module.css'

import error from '../assets/error.svg'

export interface ILoadingProps {
  message: string
}

const Loading = ({ message }: ILoadingProps) => {
  return (
    <div className={styles.container}>
      <img src={error} className={styles.error} />
      <p className={styles.message}>{message}</p>
    </div>
  )
}

export default Loading
