import { useState } from 'react'

import styles from '../css/SideNavSubDropDown.module.css'

export interface ISideNavSubDropDown {
  children: JSX.Element
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
        <p className={styles.label}>
          {/* TODO: ADD drop down icon */}
          {label} {isOpen && <span>V</span>}
          {!open && <span>{'>'}</span>}
        </p>
      </div>
      {isOpen && <div className={styles.subContainer}> {children}</div>}
    </div>
  )
}

export default SideNavSubDropDown
