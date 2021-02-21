import SideNavItem from './SideNavItem'

import styles from '../css/SideNav.module.css'

import repoIcon from '../assets/database.svg'
import reportIcon from '../assets/report.svg'
import settingsIcon from '../assets/settings.svg'

export interface ISideNavProps {
  isOpen: boolean
  sideNavToggler: (isOpen: boolean) => void
}

const items = [
  { icon: repoIcon, label: 'Projects', dest: '/home' },
  { icon: reportIcon, label: 'Reports', dest: '/reports' },
  { icon: settingsIcon, label: 'Settings', dest: '/settings' },
]

const SideNav = ({ isOpen, sideNavToggler }: ISideNavProps) => {
  const toggleSideNav = () => {
    sideNavToggler(isOpen)
  }

  return (
    <aside className={`${styles.sideNav} ${isOpen ? '' : styles.hideSideNav}`}>
      <button
        type="button"
        className={styles.closeSideNavButton}
        onClick={toggleSideNav}
      >
        {isOpen ? <>&#8249;</> : <>&#8250;</>}
      </button>
      <ul className={styles.itemList}>
        {items.map(item => {
          return (
            <SideNavItem
              key={item.label}
              icon={item.icon}
              label={item.label}
              destPath={item.dest}
            />
          )
        })}
      </ul>
    </aside>
  )
}

export default SideNav
