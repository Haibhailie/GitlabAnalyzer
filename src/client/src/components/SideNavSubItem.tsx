import { ReactNode, useState } from 'react'
import { Link } from 'react-router-dom'
import classNames from '../utils/classNames'

import styles from '../css/SideNavSubItem.module.css'

import { ReactComponent as DropdownIcon } from '../assets/dropdown-small.svg'

interface ISideNavLinkProps {
  label: string
  destPath: string
}

interface ISideNavButtonProps {
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

const SideNavLink = ({ label, destPath }: ISideNavLinkProps) => {
  return (
    <Link to={destPath} className={styles.item}>
      <p className={styles.label}>{label}</p>
    </Link>
  )
}

const SideNavButton = ({ label, onClick }: ISideNavButtonProps) => {
  return (
    <button onClick={onClick} className={styles.btn}>
      <span className={styles.item}>
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
  const [isOpen, setOpen] = useState(startOpened)

  const toggleOpen = () => {
    setOpen(!isOpen)
  }

  return (
    <>
      <button onClick={toggleOpen} className={styles.btn}>
        <span className={classNames(styles.item, !isOpen && styles.closed)}>
          <p className={styles.label}>{label}</p>
          <DropdownIcon className={classNames(styles.dropdownIcon)} />
        </span>
      </button>
      {isOpen && <div className={styles.container}>{children}</div>}
    </>
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
