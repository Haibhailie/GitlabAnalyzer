import { Link } from 'react-router-dom'

import styles from '../css/SideNavItem.module.css'

interface SideNavItemProps {
  icon: string
  label: string
  destPath: string
}

const SideNavItem = ({ icon, label, destPath }: SideNavItemProps) => {
  return (
    <div>
      <Link to={destPath} className={styles.label}>
        <li className={styles.item}>
          <img src={icon} className={styles.icon} />
          <p>{label}</p>
        </li>
      </Link>
    </div>
  )
}

export default SideNavItem
