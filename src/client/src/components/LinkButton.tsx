import { ReactNode } from 'react'
import { Link } from 'react-router-dom'

import styles from '../css/LinkButton.module.css'

export interface IButtonProps {
  children: ReactNode
  destPath: string
}

const LinkButton = ({ children, destPath }: IButtonProps) => {
  return (
    <Link to={destPath} className={styles.button}>
      {children}
    </Link>
  )
}

export default LinkButton
