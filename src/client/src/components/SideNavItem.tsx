import { Link } from 'react-router-dom'

import styles from '../css/SideNavItem.module.css'

export interface ISideNavItemProps {
  icon: string
  label: string
  destPath: string
}

const SideNavItem = ({ icon, label, destPath }: ISideNavItemProps) => {
  return (
    <>
      <Link to={destPath} className={styles.link}>
        <div className={styles.item}>
          <img src={icon} className={styles.icon} />
          <p className={styles.label}>{label}</p>
        </div>
      </Link>
    </>
  )
}

export default SideNavItem
