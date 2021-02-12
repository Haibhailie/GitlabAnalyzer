import SideNavItem from './SideNavItem'

import styles from '../css/SideNav.module.css'

import repoIcon from '../assets/database.svg'
import reportIcon from '../assets/report.svg'
import settingsIcon from '../assets/settings.svg'

const items = [
  { icon: repoIcon, label: 'Repositories', dest: '/repos' },
  { icon: reportIcon, label: 'Reports', dest: '/reports' },
  { icon: settingsIcon, label: 'Settings', dest: '/settings' },
]

const SideNav = () => {
  return (
    <aside className={styles.container}>
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
