import SideNavItem from './SideNavItem'

import styles from '../css/SideNav.module.css'

import repoIcon from '../assets/database.svg'
import reportIcon from '../assets/report.svg'
import settingsIcon from '../assets/settings.svg'

export interface ISideNavProps {
  isOpen: boolean
  openSideNav: (isOpen: boolean) => void
}

const items = [
  { icon: repoIcon, label: 'Projects', dest: '/projects' },
  { icon: reportIcon, label: 'Reports', dest: '/reports' },
  { icon: settingsIcon, label: 'Settings', dest: '/settings' },
]

const SideNav = ({ isOpen, openSideNav }: ISideNavProps) => {
  const toggleSideNav = () => {
    openSideNav(isOpen)
  }

  return (
    <aside className={isOpen ? styles.sideBarOpen : styles.sideBarHidden}>
      <button
        type="button"
        className={styles.closeSideNavButton}
        onClick={toggleSideNav}
      >
        &#8249;
      </button>
      <ul className={styles.itemList}>
        {items.map((item, index) => {
          return (
            <SideNavItem
              key={index}
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
