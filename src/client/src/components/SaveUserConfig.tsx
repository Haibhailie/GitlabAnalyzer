import { ChangeEvent, useContext, useState } from 'react'

import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'
import SideNavSubDropDown from './SideNavSubDropDown'

import styles from '../css/SaveUserConfig.module.css'

interface ISaveUserConfig {
  setCurrentConfig: (newUserConfig: IUserConfig) => void
}
const SaveUserConfig = ({ setCurrentConfig }: ISaveUserConfig) => {
  // TODO: fetch array of saved configs
  const dummySavedConfigs: Array<IUserConfig> = [
    {
      startDate: new Date('2020-10-05'),
      endDate: new Date('2020-11-10'),
      projectGraphBy: 'Split By Member',
      scoreBy: 'Commits',
      graphYAxis: 'Score',
      generalScores: [
        { type: 'New line of code', value: 1 },
        { type: 'Deleting a line', value: 1 },
        { type: 'Comment/blank', value: 1 },
        { type: 'Spacing change', value: 1 },
        { type: 'Syntax only', value: 1 },
      ],
      fileScores: [
        { fileExtension: '.java', scoreMultiplier: 2 },
        { fileExtension: '.html', scoreMultiplier: 0 },
        { fileExtension: '.tsx', scoreMultiplier: 0 },
        { fileExtension: '.css', scoreMultiplier: 0.2 },
      ],
      name: 'Iteration 1',
    },
    {
      startDate: new Date('2021-02-05'),
      endDate: new Date('2020-03-10'),
      projectGraphBy: 'Split By Member',
      scoreBy: 'Merge Requests',
      graphYAxis: 'Score',
      generalScores: [
        { type: 'New line of code', value: 0 },
        { type: 'Deleting a line', value: 0 },
        { type: 'Comment/blank', value: 0 },
        { type: 'Spacing change', value: 0 },
        { type: 'Syntax only', value: 0 },
      ],
      fileScores: [
        { fileExtension: '.java', scoreMultiplier: 0 },
        { fileExtension: '.html', scoreMultiplier: 0 },
        { fileExtension: '.tsx', scoreMultiplier: 1 },
        { fileExtension: '.css', scoreMultiplier: 1 },
      ],
      name: 'Iteration 2',
    },
  ]

  const { userConfig, dispatch } = useContext(UserConfigContext)

  const [name, setName] = useState(userConfig.name)
  const [savedConfigs, setSavedConfigs] = useState(dummySavedConfigs)

  const nameChange = (event: ChangeEvent<HTMLInputElement>) => {
    const newName = event.target.value
    setName(newName)
    dispatch({ type: 'SET_NAME', name: newName })
  }

  const save = () => {
    const newSavedConfigs = savedConfigs
    newSavedConfigs.push(userConfig)
    setSavedConfigs([...newSavedConfigs])
  }

  const deleteConfig = (index: number) => {
    // TODO: POST removed list
    const configs = savedConfigs
    configs.splice(index, 1)
    setSavedConfigs([...configs])
  }

  const loadConfig = (config: IUserConfig) => {
    setCurrentConfig(config)
    setName(config.name)
    // TODO: POST new list
  }

  return (
    // TODO: ADD ICONS
    <div className={styles.container}>
      <div>X Save Configuration</div>
      <input
        value={name}
        onChange={nameChange}
        className={styles.input}
        placeholder="Name config..."
      />
      <div className={styles.saveButton} onClick={save}>
        X Save Config
      </div>
      <SideNavSubDropDown startOpened={true} label="Load Configuration">
        <>
          <ul className={styles.list}>
            {savedConfigs.map((config, index) => (
              <li key={index} className={styles.item}>
                <div
                  className={styles.label}
                  onClick={() => loadConfig(config)}
                >
                  {config.name}
                </div>
                <div
                  className={styles.deleteButton}
                  onClick={() => deleteConfig(index)}
                >
                  x
                </div>
              </li>
            ))}
          </ul>
        </>
      </SideNavSubDropDown>
    </div>
  )
}

export default SaveUserConfig
