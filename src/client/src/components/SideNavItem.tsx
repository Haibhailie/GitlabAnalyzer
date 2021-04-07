import { ReactNode } from 'react'
import { Link } from 'react-router-dom'

import Dropdown from './Dropdown'

import styles from '../css/SideNavItem.module.css'

interface ISideNavLinkProps {
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
  destPath: string
}

interface ISideNavDropdownProps {
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
  children: ReactNode
  startOpened?: boolean
}

export type ISideNavItemProps = ISideNavLinkProps | ISideNavDropdownProps

const SideNavLink = ({ Icon, label, destPath }: ISideNavLinkProps) => {
  return (
    <Link to={destPath} className={styles.item}>
      <Icon className={styles.icon} />
      <p className={styles.label}>{label}</p>
    </Link>
  )
}

const SideNavDropdown = ({
  Icon,
  label,
  children,
  startOpened,
}: ISideNavDropdownProps) => {
  return (
    <Dropdown
      startOpened={startOpened}
      header={
        <span className={styles.dropdown}>
          <Icon className={styles.icon} />
          <p className={styles.label}>{label}</p>
        </span>
      }
    >
      {children}
    </Dropdown>
  )
}

const SideNavItem = (props: ISideNavItemProps) => {
  if ('destPath' in props) {
    return <SideNavLink {...props} />
  } else {
    return <SideNavDropdown {...props} />
  }
}

export default SideNavItem
