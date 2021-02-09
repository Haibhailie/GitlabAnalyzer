import styles from '../css/Header.module.css'
import planet from '../assets/planet-icon.svg'
import menuIcon from '../assets/icons8-menu-50.png'

const Header: React.FC = () => (
  <div className={styles.div}>
    <button className={styles.hamburgerMenu}>
      <img src={menuIcon} />
    </button>
    <img src={planet} />
    <h1>GitLab Analyzer</h1>
  </div>
)

export default Header
