import { ReactNode, useState } from 'react'
import { Link } from 'react-router-dom'
import classNames from '../utils/classNames'

import styles from '../css/SideNavItem.module.css'

import { ReactComponent as DropdownIcon } from '../assets/dropdown-large.svg'

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
  const [isOpen, setOpen] = useState(startOpened)

  const toggleOpen = () => {
    setOpen(!isOpen)
  }

  return (
    <>
      <button onClick={toggleOpen} className={styles.btn}>
        <span className={classNames(styles.dropdown, !isOpen && styles.closed)}>
          <Icon className={styles.icon} />
          <p className={styles.label}>{label}</p>
          <DropdownIcon className={classNames(styles.dropdownIcon)} />
        </span>
      </button>
      {isOpen && <div className={styles.container}>{children}</div>}
    </>
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
