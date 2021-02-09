import styles from '../css/Header.module.css'
import planet from '../assets/planet-icon.svg'

const Header: React.FC = () => (
  <div className={styles.div}>
    <img src={planet} />
    <h1>GitLab Analyzer</h1>
  </div>
)

export default Header
