import { Link } from 'react-router-dom'

import styles from '../css/SideNavItem.module.css'

export interface ISideNavItemProps {
  icon: string
  label: string
  destPath: string
}

const SideNavItem = ({ icon, label, destPath }: ISideNavItemProps) => {
  return (
    <div>
      <Link to={destPath} className={styles.link}>
        <li className={styles.item}>
          <img src={icon} className={styles.icon} />
          <p className={styles.label}>{label}</p>
        </li>
      </Link>
    </div>
  )
}

export default SideNavItem
