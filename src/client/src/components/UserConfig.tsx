import React, { ChangeEvent, useContext, useState } from 'react'
import {
  IUserConfig,
  SET_CONFIG,
  SET_GRAPH_BY,
  TGraphMode,
  TYAxis,
  UserConfigContext,
} from '../context/UserConfigContext'

import TextField from '@material-ui/core/TextField'
import SideNavSubDropDown from './SideNavSubDropDown'
import UserConfigPopup from './UserConfigPopup'
import SavedConfigs from './SavedConfigs'
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
      graphMode: 'MEMBER',
      scoreBy: 'COMMITS',
      yAxis: 'SCORE',
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
      graphMode: 'PROJECT',
      scoreBy: 'MRS',
      yAxis: 'SCORE',
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

  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const [fileScores, setFileScores] = useState(userConfigs.selected.fileScores)
  const [generalScores, setGeneralScores] = useState(
    userConfigs.selected.generalScores
  )

  const [popUpOpen, setPopUpOpen] = useState(false)
  const [dateError, setDateError] = useState(false)

  const [name, setName] = useState(userConfigs.selected.name)
  const [isUniqueName, setIsUniqueName] = useState(true)
  const [savedConfigs, setSavedConfigs] = useState(dummySavedConfigs)

  const setSavedConfigsHandler = (configs: IUserConfig[]) => {
    setSavedConfigs(configs)
  }

  const startDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    dispatch({
      type: 'SET_START_DATE',
      date: newValue,
    })

    if (validDates(newValue, userConfigs.selected.endDate)) {
      // TODO: Add update userconfig post and get new projects by date
    }
  }
  const endDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    dispatch({
      type: 'SET_END_DATE',
      date: newValue,
    })
    if (validDates(userConfigs.selected.startDate, newValue)) {
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
    const newValue = event.target.value as TYAxis
    dispatch({
      type: 'SET_GRAPH_Y_AXIS',
      yAxis: newValue,
    })
  }

  const projectGraphByChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    // TODO: Add update userconfig post
    const newValue = event.target.value as TGraphMode
    dispatch({
      type: SET_GRAPH_BY,
      graphMode: newValue,
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
    newSavedConfigs.push(userConfigs.selected)
    setSavedConfigs([...newSavedConfigs])
    setName('')
  }

  const setCurrentConfig = (newUserConfig: IUserConfig) => {
    setFileScores(newUserConfig.fileScores)
    setGeneralScores(newUserConfig.generalScores)
    if (newUserConfig.id) {
      dispatch({
        type: SET_CONFIG,
        id: newUserConfig.id,
      })
    }
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
                    userConfigs.selected.startDate
                      ? formatDate(userConfigs.selected.startDate)
                      : ''
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
                    userConfigs.selected.endDate
                      ? formatDate(userConfigs.selected.endDate)
                      : ''
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
                  checked={userConfigs.selected.scoreBy === 'MRS'}
                  onChange={memberScoreByChange}
                />
                <label className={styles.label}>Merge Requests</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Commits"
                  checked={userConfigs.selected.scoreBy === 'COMMITS'}
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
                  value="NUMBER"
                  checked={userConfigs.selected.yAxis === 'NUMBER'}
                  onChange={graphYAxisChange}
                />
                <label className={styles.label}>Number</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="SCORE"
                  checked={userConfigs.selected.yAxis === 'SCORE'}
                  onChange={graphYAxisChange}
                />
                <label className={styles.label}>Score</label>
              </div>
              <p className={styles.subHeader}>Project Graph</p>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Entire Project"
                  checked={userConfigs.selected.graphMode === 'PROJECT'}
                  onChange={projectGraphByChange}
                />
                <label className={styles.label}>Entire Project</label>
              </div>
              <div className={styles.inputField}>
                <input
                  type="radio"
                  value="Split By Member"
                  checked={userConfigs.selected.graphMode === 'MEMBER'}
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
        <SavedConfigs />
      </SideNavDropDown>
    </>
  )
}

export default UserConfig
