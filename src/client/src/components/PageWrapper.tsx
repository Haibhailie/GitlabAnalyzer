import Header from './Header'

import { useState } from 'react'

const PageWrapper = () => {
  const [sideNavOpen, setSideNavOpen] = useState(true)

  const toggleSideNav = () => {
    setSideNavOpen(!sideNavOpen)
  }

  return (
    <div>
      <Header isOpen={sideNavOpen} sideNavToggler={toggleSideNav} />
    </div>
  )
}

export default PageWrapper
