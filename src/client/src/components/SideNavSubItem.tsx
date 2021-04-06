import { ReactNode } from 'react'
import { Link } from 'react-router-dom'

import Dropdown from './Dropdown'

import styles from '../css/SideNavSubItem.module.css'

interface ISideNavLinkProps {
  Icon?: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
  destPath: string
}

interface ISideNavButtonProps {
  Icon?: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
  onClick: (event: unknown) => void
}

interface ISideNavDropdownProps {
  label: string
  children: ReactNode
  startOpened?: boolean
}

export type ISideNavSubItemProps =
  | ISideNavLinkProps
  | ISideNavButtonProps
  | ISideNavDropdownProps

const SideNavLink = ({ label, destPath, Icon }: ISideNavLinkProps) => {
  return (
    <Link to={destPath} className={styles.item}>
      {Icon && <Icon className={styles.icon} />}
      <p className={styles.label}>{label}</p>
    </Link>
  )
}

const SideNavButton = ({ label, onClick, Icon }: ISideNavButtonProps) => {
  return (
    <button onClick={onClick} className={styles.btn}>
      <span className={styles.item}>
        {Icon && <Icon className={styles.icon} />}
        <p className={styles.label}>{label}</p>
      </span>
    </button>
  )
}

const SideNavDropdown = ({
  label,
  children,
  startOpened,
}: ISideNavDropdownProps) => {
  console.log(startOpened)
  return (
    <Dropdown
      header={<p className={styles.item}>{label}</p>}
      startOpened={startOpened}
    >
      <div className={styles.container}>{children}</div>
    </Dropdown>
  )
}

const SideNavSubItem = (props: ISideNavSubItemProps) => {
  if ('destPath' in props) {
    return <SideNavLink {...props} />
  } else if ('onClick' in props) {
    return <SideNavButton {...props} />
  } else {
    return <SideNavDropdown {...props} />
  }
}

export default SideNavSubItem
