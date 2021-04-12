import jsonFetcher from '../utils/jsonFetcher'
import { useHistory } from 'react-router-dom'

import styles from '../css/LogoutButton.module.css'

import { ReactComponent as LogoutIcon } from '../assets/logout.svg'

const LogoutButton = () => {
  const history = useHistory()
  const handleLogout = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    jsonFetcher('/api/signout', {
      responseIsEmpty: true,
      method: 'POST',
    })
      .then(resCode => {
        if (resCode === 200) {
          history.push('/')
        }
      })
      .catch(error => {
        alert(error.message)
      })
  }

  return (
    <button className={styles.button} onClick={handleLogout}>
      <LogoutIcon className={styles.icon} />
      Log out
    </button>
  )
}

export default LogoutButton
