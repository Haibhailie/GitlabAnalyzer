import { Link } from 'react-router-dom'

interface SideNavItemProps {
  icon: string
  label: string
  destPath: string
}

const SideNavItem = ({ icon, label, destPath }: SideNavItemProps) => {
  return (
    <li>
      <Link to={destPath}>
        <img src={icon} />
        {label}
      </Link>
    </li>
  )
}

export default SideNavItem
