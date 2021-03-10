import { useState } from 'react'

import styles from '../css/SideNavDropDown.module.css'

export interface ISideNavDropDown {
  children: JSX.Element
  icon: string
  label: string
}

const SideNavDropDown = ({ children, icon, label }: ISideNavDropDown) => {
  const [open, setOpen] = useState(false)

  const toggleTab = () => {
    setOpen(!open)
  }

  return (
    <div>
      <div className={styles.item} onClick={toggleTab}>
        <img src={icon} className={styles.icon} />
        <p className={styles.label}>{label}</p>
        {open && <span>V</span>}
        {!open && <span>{'>'}</span>}
      </div>
      <div className={styles.container}>{open && children}</div>
    </div>
  )
}

export default SideNavDropDown
