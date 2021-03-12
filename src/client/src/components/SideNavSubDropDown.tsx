import { useState } from 'react'

import styles from '../css/SideNavSubDropDown.module.css'

export interface ISideNavSubDropDown {
  children: JSX.Element
  initial: boolean
  label: string
}
const SideNavSubDropDown = ({
  children,
  initial,
  label,
}: ISideNavSubDropDown) => {
  const [open, setOpen] = useState(initial)

  const toggleTab = () => {
    setOpen(!open)
  }

  return (
    <div>
      <div className={styles.header} onClick={toggleTab}>
        <p className={styles.label}>
          {/* TODO: ADD drop down icon */}
          {label} {open && <span>V</span>}
          {!open && <span>{'>'}</span>}
        </p>
      </div>
      <div className={styles.subContainer}>{open && children}</div>
    </div>
  )
}

export default SideNavSubDropDown
