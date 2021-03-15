import { useState } from 'react'
import classNames from '../utils/classNames'

import Header from './Header'
import SideNav from './SideNav'

import styles from '../css/PageWrapper.module.css'

export interface IPageWrapperProps {
  children: JSX.Element
}

const PageWrapper = ({ children }: IPageWrapperProps) => {
  const [isSideNavOpen, setSideNavOpen] = useState(true)

  const toggleSideNav = () => {
    setSideNavOpen(!isSideNavOpen)
  }

  return (
    <div
      className={classNames(
        styles.wrapper,
        !isSideNavOpen && styles.closedSideNav
      )}
    >
      <Header sideNavToggler={toggleSideNav} className={styles.header} />
      <main className={styles.main}>{children}</main>
      <SideNav
        isOpen={isSideNavOpen}
        sideNavToggler={toggleSideNav}
        className={styles.sideNav}
      />
    </div>
  )
}

export default PageWrapper
