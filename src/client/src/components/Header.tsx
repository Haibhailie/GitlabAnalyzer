import { Link } from 'react-router-dom'
import classNames from '../utils/classNames'

import styles from '../css/Header.module.css'

import planet from '../assets/planet-icon.svg'
import menuIcon from '../assets/menu-icon.svg'

export interface IHeaderProps {
  sideNavToggler: () => void
  className?: string
}

const Header = ({ sideNavToggler, className }: IHeaderProps) => {
  const toggleSideNav = () => {
    sideNavToggler()
  }

  return (
    <div className={classNames(styles.headerDiv, className)}>
      <button className={styles.hamburgerMenu} onClick={toggleSideNav}>
        <img src={menuIcon} className={styles.hamburgerMenuIcon} />
      </button>
      <Link to="/home" className={styles.homeLink}>
        <img src={planet} className={styles.logo} />
        <h1 className={styles.appName}>GitLab Analyzer</h1>
      </Link>
    </div>
  )
}

export default Header
