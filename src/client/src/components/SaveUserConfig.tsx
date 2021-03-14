import { useContext } from 'react'

import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'
import SideNavSubDropDown from './SideNavSubDropDown'
import SideNavDropDown from './SideNavDropDown'

import styles from '../css/SaveUserConfig.module.css'

import { ReactComponent as Delete } from '../assets/delete.svg'

interface ISaveUserConfig {
  setCurrentConfig: (newUserConfig: IUserConfig) => void
  savedConfigs: IUserConfig[]
  setSavedConfigsHandler: (configs: IUserConfig[]) => void
  checkUniqueName: (name: string) => boolean
}

const SaveUserConfig = ({
  setCurrentConfig,
  savedConfigs,
  setSavedConfigsHandler,
  checkUniqueName,
}: ISaveUserConfig) => {
  // TODO: fetch array of saved configs
  const { userConfig, dispatch } = useContext(UserConfigContext)

  const deleteConfig = (index: number) => {
    // TODO: POST removed list
    const configs = savedConfigs
    configs.splice(index, 1)
    setSavedConfigsHandler([...configs])
    checkUniqueName(userConfig.name)
  }

  const loadConfig = (config: IUserConfig) => {
    setCurrentConfig(config)
    // TODO: POST new list
  }

  return (
    <>
      <ul className={styles.list}>
        {savedConfigs.map((config, index) => (
          <li key={config.name} className={styles.item}>
            <button className={styles.label} onClick={() => loadConfig(config)}>
              {config.name}
            </button>
            <button className={styles.deleteButton}>
              <Delete onClick={() => deleteConfig(index)} />
            </button>
          </li>
        ))}
      </ul>
    </>
  )
}

export default SaveUserConfig
