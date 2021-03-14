import { useContext } from 'react'

import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'

import styles from '../css/SaveUserConfig.module.css'

import { ReactComponent as Delete } from '../assets/delete.svg'

export interface ISavedConfigsProps {
  setCurrentConfig: (newUserConfig: IUserConfig) => void
  savedConfigs: IUserConfig[]
  setSavedConfigsHandler: (configs: IUserConfig[]) => void
  checkUniqueName: (name: string) => boolean
}

const SavedConfigs = ({
  setCurrentConfig,
  savedConfigs,
  setSavedConfigsHandler,
  checkUniqueName,
}: ISavedConfigsProps) => {
  // TODO: fetch array of saved configs
  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const deleteConfig = (index: number) => {
    // TODO: POST removed list
    const configs = savedConfigs
    configs.splice(index, 1)
    setSavedConfigsHandler([...configs])
    checkUniqueName(userConfigs.selected.name)
  }

  const loadConfig = (config: IUserConfig) => {
    setCurrentConfig(config)
    // TODO: POST new list
  }

  return (
    <div className={styles.list}>
      {savedConfigs.map((config, index) => (
        <div key={config.name} className={styles.item}>
          <button className={styles.label} onClick={() => loadConfig(config)}>
            {config.name}
          </button>
          <button className={styles.deleteButton}>
            <Delete onClick={() => deleteConfig(index)} />
          </button>
        </div>
      ))}
    </div>
  )
}

export default SavedConfigs
