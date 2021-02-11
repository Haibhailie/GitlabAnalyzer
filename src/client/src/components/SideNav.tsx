import SideNavItem from './SideNavItem'

import styles from '../css/SideNav.module.css'

import repoIcon from '../assets/database.svg'
import reportIcon from '../assets/report.svg'

const items = [
  { icon: repoIcon, label: 'Repositories', dest: '/repos' },
  { icon: reportIcon, label: 'Reports', dest: '/reports' },
]

const SideNav = () => {
  return (
    <div className={styles.container}>
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
    </div>
  )
}

export default SideNav
