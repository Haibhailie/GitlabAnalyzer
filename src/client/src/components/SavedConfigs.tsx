import { useContext } from 'react'

import {
  DELETE_CONFIG,
  IUserConfig,
  SET_CONFIG,
  UserConfigContext,
} from '../context/UserConfigContext'

import styles from '../css/SaveUserConfig.module.css'

import { ReactComponent as DeleteIcon } from '../assets/delete.svg'

const SavedConfigs = () => {
  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const deleteConfig = (id?: string) =>
    id && dispatch({ type: DELETE_CONFIG, id })

  const loadConfig = (config: IUserConfig) => {
    if (config.id) {
      dispatch({ type: SET_CONFIG, id: config.id })
    }
  }

  return (
    <div className={styles.list}>
      {Object.values(userConfigs.configs).map(config => (
        <div key={config.name} className={styles.item}>
          <button className={styles.label} onClick={() => loadConfig(config)}>
            {config.name}
          </button>
          <button
            className={styles.deleteButton}
            onClick={() => deleteConfig(config.id)}
          >
            <DeleteIcon />
          </button>
        </div>
      ))}
    </div>
  )
}

export default SavedConfigs
