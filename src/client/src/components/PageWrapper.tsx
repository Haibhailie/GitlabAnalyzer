import { useState } from 'react'

import Header from './Header'
import SideNav from './SideNav'

const PageWrapper = () => {
  const [sideNavOpen, setSideNavOpen] = useState(true)

  const toggleSideNav = () => {
    setSideNavOpen(!sideNavOpen)
  }

  return (
    <div>
      <Header isOpen={sideNavOpen} sideNavToggler={toggleSideNav} />
      <SideNav isOpen={sideNavOpen} sideNavToggler={toggleSideNav} />
    </div>
  )
}

export default PageWrapper
