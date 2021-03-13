import { useState } from 'react'

import styles from '../css/SideNavSubDropDown.module.css'

import { ReactComponent as Dropdown } from '../assets/dropdown-small.svg'

export interface ISideNavSubDropDown {
  children: unknown
  startOpened: boolean
  label: string
}
const SideNavSubDropDown = ({
  children,
  startOpened,
  label,
}: ISideNavSubDropDown) => {
  const [isOpen, setIsOpen] = useState(startOpened)

  const toggleTab = () => {
    setIsOpen(!isOpen)
  }

  return (
    <div>
      <div className={styles.header} onClick={toggleTab}>
        <div>{label}</div>
        <Dropdown className={isOpen ? styles.openIcon : styles.closedIcon} />
      </div>
      {isOpen && <div className={styles.subContainer}> {children}</div>}
    </div>
  )
}

export default SideNavSubDropDown
