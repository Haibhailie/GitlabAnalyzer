import { Link } from 'react-router-dom'

import styles from '../css/Header.module.css'

import planet from '../assets/planet-icon.svg'
import menuIcon from '../assets/icons8-menu.svg'

const Header = () => (
  <div className={styles.headerDiv}>
    <button className={styles.hamburgerMenu}>
      <img src={menuIcon} />
    </button>
    <Link to="/home">
      <img src={planet} className={styles.logo} />
      <h1>GitLab Analyzer</h1>
    </Link>
  </div>
)

export default Header
