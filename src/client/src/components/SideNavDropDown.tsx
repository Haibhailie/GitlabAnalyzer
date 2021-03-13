import { useState } from 'react'

import styles from '../css/SideNavDropDown.module.css'

import { ReactComponent as Dropdown } from '../assets/dropdown-large.svg'

export interface ISideNavDropDown {
  children: JSX.Element
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
  label: string
}

const SideNavDropDown = ({ children, Icon, label }: ISideNavDropDown) => {
  const [isOpen, setIsOpen] = useState(false)

  const toggleTab = () => {
    setIsOpen(!isOpen)
  }

  return (
    <>
      <div className={styles.item} onClick={toggleTab}>
        <Icon className={styles.icon} />
        <p className={styles.label}>{label}</p>
        <Dropdown className={isOpen ? styles.openIcon : styles.closedIcon} />
      </div>
      <div className={styles.container}>{isOpen && children}</div>
    </>
  )
}

export default SideNavDropDown
