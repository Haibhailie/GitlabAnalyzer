import { Link } from 'react-router-dom'

import styles from '../css/SideNavItem.module.css'

export interface ISideNavItemProps {
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
  destPath: string
}

const SideNavItem = ({ Icon, label, destPath }: ISideNavItemProps) => {
  return (
    <Link to={destPath} className={styles.item}>
      <Icon className={styles.icon} />
      <p className={styles.label}>{label}</p>
    </Link>
  )
}

export default SideNavItem
