import React, { ChangeEvent, useContext, useState } from 'react'
import TextField from '@material-ui/core/TextField'

import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'
import SideNavSubDropDown from './SideNavSubDropDown'
import UserConfigPopup from './UserConfigPopup'
import SaveUserConfig from './SaveUserConfig'
import SideNavDropDown from './SideNavDropDown'

import styles from '../css/UserConfig.module.css'

import { ReactComponent as SaveLarge } from '../assets/save-large.svg'
import { ReactComponent as SaveSmall } from '../assets/save-small.svg'
import { ReactComponent as Edit } from '../assets/edit.svg'
import { ReactComponent as toolIcon } from '../assets/tool.svg'
import { ReactComponent as settingsIcon } from '../assets/settings.svg'

const UserConfig = () => {
  const dummySavedConfigs: IUserConfig[] = [
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

  const [fileScores, setFileScores] = useState(userConfig.fileScores)
  const [generalScores, setGeneralScores] = useState(userConfig.generalScores)

  const [popUpOpen, setPopUpOpen] = useState(false)
  const [dateError, setDateError] = useState(false)

  const [name, setName] = useState(userConfig.name)
  const [isUniqueName, setIsUniqueName] = useState(true)
  const [savedConfigs, setSavedConfigs] = useState(dummySavedConfigs)

  const setSavedConfigsHandler = (configs: IUserConfig[]) => {
    setSavedConfigs(configs)
  }

  const startDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    dispatch({
      type: 'SET_START_DATE',
      startDate: newValue,
    })

    if (validDates(newValue, userConfig.endDate)) {
      // TODO: Add update userconfig post and get new projects by date
    }
  }
  const endDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    dispatch({
      type: 'SET_END_DATE',
      endDate: newValue,
    })
    if (validDates(userConfig.startDate, newValue)) {
      // TODO: Add update userconfig post and get new projects by date
    }
  }

  const validDates = (start?: Date, end?: Date) => {
    if (start && end && start > end) {
      setDateError(true)
      return false
    } else {
      setDateError(false)
      return true
    }
  }

  const formatDate = (date: Date) => {
    if (date) {
      return date.toISOString().split('T')[0]
    } else {
      return
    }
  }

  const memberScoreByChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    // TODO: Add update userconfig post
    const newValue = event.target.value as IUserConfig['scoreBy']
    dispatch({
      type: 'SET_SCORE_BY',
      scoreBy: newValue,
    })
  }

  const graphYAxisChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    // TODO: Add update userconfig post
    const newValue = event.target.value as IUserConfig['graphYAxis']
    dispatch({
      type: 'SET_GRAPH_Y_AXIS',
      graphYAxis: newValue,
    })
  }

  const projectGraphByChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    // TODO: Add update userconfig post
    const newValue = event.target.value as IUserConfig['projectGraphBy']
    dispatch({
      type: 'SET_PROJECT_GRAPH_BY',
      projectGraphBy: newValue,
    })
  }

  const checkUniqueName = (newName: string) => {
    const currentNames = savedConfigs.map(config => config.name)
    if (!(currentNames.indexOf(newName) > -1)) {
      setIsUniqueName(true)
      return true
    } else {
      setIsUniqueName(false)
      return false
    }
  }

  const nameChange = (event: ChangeEvent<HTMLInputElement>) => {
    const newName = event.target.value
    setName(newName)
    if (checkUniqueName(newName)) {
      dispatch({ type: 'SET_CONFIG_NAME', name: newName })
    }
  }

  const save = () => {
    const newSavedConfigs = savedConfigs
    newSavedConfigs.push(userConfig)
    setSavedConfigs([...newSavedConfigs])
    setName('')
  }

  const setCurrentConfig = (newUserConfig: IUserConfig) => {
    setFileScores(newUserConfig.fileScores)
    setGeneralScores(newUserConfig.generalScores)
    dispatch({
      type: 'SET_USER_CONFIG',
      userConfig: newUserConfig,
    })
  }

  const togglePopup = () => {
    setPopUpOpen(!popUpOpen)
  }

  return (
    <>
      <SideNavDropDown label="Settings" Icon={settingsIcon}>
        <>
          <SideNavSubDropDown startOpened={true} label="Date Range">
            <>
              <div className={styles.dateContainer}>
                <TextField
                  type="date"
                  name="startDate"
                  label="Start date"
                  variant="outlined"
                  size="small"
                  margin="dense"
                  InputProps={{
                    inputProps: { min: '2005-01-01' },
                    style: { height: '32px' },
                  }}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  className={styles.dateInput}
                  onChange={startDateChange}
                  value={
                    userConfig.startDate ? formatDate(userConfig.startDate) : ''
                  }
                />
              </div>
              <div className={styles.dateContainer}>
                <TextField
                  type="date"
                  name="endDate"
                  label="End Date"
                  variant="outlined"
                  size="small"
                  InputProps={{
                    inputProps: { min: '2005-01-01' },
                    style: { height: '32px' },
                  }}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  FormHelperTextProps={{ style: { margin: '2px 4px 0px 4px' } }}
                  className={styles.dateInput}
                  onChange={endDateChange}
                  value={
                    userConfig.endDate ? formatDate(userConfig.endDate) : ''
                  }
                  error={dateError}
                  helperText={
                    dateError
                      ? 'Must come after start date'
                      : 'To filter, fill in both fields'
                  }
                />
              </div>
            </>
          </SideNavSubDropDown>
          <SideNavSubDropDown startOpened={true} label="Member scores by">
            <>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Merge Requests"
                  checked={userConfig.scoreBy === 'Merge Requests'}
                  onChange={memberScoreByChange}
                />
                <label className={styles.label}>Merge Requests</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Commits"
                  checked={userConfig.scoreBy === 'Commits'}
                  onChange={memberScoreByChange}
                />
                <label className={styles.label}>Commits</label>
              </div>
            </>
          </SideNavSubDropDown>
          <SideNavSubDropDown startOpened={true} label="Graph Settings">
            <>
              <p className={styles.subHeader}>Graph Y-Axis</p>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Number"
                  checked={userConfig.graphYAxis === 'Number'}
                  onChange={graphYAxisChange}
                />
                <label className={styles.label}>Number</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Score"
                  checked={userConfig.graphYAxis === 'Score'}
                  onChange={graphYAxisChange}
                />
                <label className={styles.label}>Score</label>
              </div>
              <p className={styles.subHeader}>Project Graph</p>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Entire Project"
                  checked={userConfig.projectGraphBy === 'Entire Project'}
                  onChange={projectGraphByChange}
                />
                <label className={styles.label}>Entire Project</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Split By Member"
                  checked={userConfig.projectGraphBy === 'Split By Member'}
                  onChange={projectGraphByChange}
                />
                <label className={styles.label}>Split By Member</label>
              </div>
            </>
          </SideNavSubDropDown>
          <button className={styles.header} onClick={togglePopup}>
            <Edit className={styles.editIcon} /> Edit Scoring
          </button>
          {popUpOpen && (
            <UserConfigPopup
              generalScores={generalScores}
              setGeneralScores={setGeneralScores}
              fileScores={fileScores}
              setFileScores={setFileScores}
              togglePopup={togglePopup}
            />
          )}
          <div className={styles.saveContainer}>
            <div className={styles.saveLabel}>
              <SaveLarge className={styles.saveIcon} /> Save Configuration
            </div>
            <input
              value={name}
              onChange={nameChange}
              className={styles.nameInput}
              placeholder="Name config..."
            />
            {!isUniqueName && (
              <div className={styles.error}>Name already exists</div>
            )}
            <button
              className={styles.saveButton}
              onClick={save}
              disabled={!isUniqueName || !name}
            >
              <SaveSmall className={styles.saveIcon} /> Save Config
            </button>
          </div>
        </>
      </SideNavDropDown>
      <SideNavDropDown Icon={toolIcon} label="Saved Configs">
        <SaveUserConfig
          setCurrentConfig={setCurrentConfig}
          savedConfigs={savedConfigs}
          setSavedConfigsHandler={setSavedConfigsHandler}
          checkUniqueName={checkUniqueName}
        />
      </SideNavDropDown>
    </>
  )
}

export default UserConfig
