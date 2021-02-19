import { useState } from 'react'

import Header from './Header'
import SideNav from './SideNav'

import styles from '../css/PageWrapper.module.css'

export interface IPageWrapperProps {
  children: JSX.Element
}

const PageWrapper = ({ children }: IPageWrapperProps) => {
  const [sideNavOpen, setSideNavOpen] = useState(true)

  const toggleSideNav = () => {
    setSideNavOpen(!sideNavOpen)
  }

  return (
    <>
      <Header isOpen={sideNavOpen} sideNavToggler={toggleSideNav} />
      <SideNav isOpen={sideNavOpen} sideNavToggler={toggleSideNav} />
      <main
        className={`${styles.main} ${sideNavOpen ? styles.mainPushed : ''}`}
      >
        {children}
      </main>
    </>
  )
}

export default PageWrapper
