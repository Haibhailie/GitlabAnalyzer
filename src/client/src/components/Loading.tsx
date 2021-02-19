import styles from '../css/Loading.module.css'

import spinner from '../assets/spinner.svg'

export interface ILoadingProps {
  message: string
}

const Loading = ({ message }: ILoadingProps) => {
  return (
    <div className={styles.container}>
      <img src={spinner} className={styles.loader} />
      <p className={styles.message}>{message}</p>
    </div>
  )
}

export default Loading
