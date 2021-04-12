import classNames from '../utils/classNames'

import SideNavItem from '../components/SideNavItem'
import UserConfig from '../components/UserConfig'
import LogoutButton from '../components/LogoutButton'

import styles from '../css/SideNav.module.css'

import { ReactComponent as repoIcon } from '../assets/database.svg'

export interface ISideNavProps {
  isOpen: boolean
  sideNavToggler: () => void
  className?: string
}

const items = [{ icon: repoIcon, label: 'Projects', dest: '/home' }]

const SideNav = ({ isOpen, sideNavToggler, className }: ISideNavProps) => {
  const toggleSideNav = () => {
    sideNavToggler()
  }

  return (
    <aside className={classNames(styles.sideNav, className)}>
      <div className={styles.scrollContainer}>
        {items.map(item => (
          <SideNavItem
            key={item.label}
            Icon={item.icon}
            label={item.label}
            destPath={item.dest}
          />
        ))}
        <UserConfig />
        <LogoutButton />
      </div>
      <button
        type="button"
        className={styles.closeSideNavButton}
        onClick={toggleSideNav}
      >
        {isOpen ? <>&#8249;</> : <>&#8250;</>}
      </button>
    </aside>
  )
}

export default SideNav
