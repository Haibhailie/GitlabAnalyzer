import { Link } from 'react-router-dom'

import styles from '../css/Button.module.css'

export interface IButtonProps {
  message: string
  destPath: string
}

const Button = ({ message, destPath }: IButtonProps) => {
  return (
    <Link to={destPath}>
      <button className={styles.button}>{message}</button>
    </Link>
  )
}

export default Button
