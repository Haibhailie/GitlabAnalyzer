import React, { useContext, useState } from 'react'
import TextField from '@material-ui/core/TextField'

import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'
import SideNavSubDropDown from './SideNavSubDropDown'
import UserConfigPopup from './UserConfigPopup'
import SaveUserConfig from './SaveUserConfig'

import styles from '../css/UserConfig.module.css'

const UserConfig = () => {
  const { userConfig, dispatch } = useContext(UserConfigContext)

  const [fileScores, setFileScores] = useState(userConfig.fileScores)
  const [generalScores, setGeneralScores] = useState(userConfig.generalScores)

  const [popUpOpen, setPopUpOpen] = useState(false)
  const [dateError, setDateError] = useState(false)

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
    <div className={styles.container}>
      <SideNavSubDropDown initial={true} label="Date Range">
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
              value={userConfig.endDate ? formatDate(userConfig.endDate) : ''}
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
      <SideNavSubDropDown initial={true} label="Member scores by">
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
      <SideNavSubDropDown initial={true} label="Graph Settings">
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
      <div className={styles.header} onClick={togglePopup}>
        {/* TODO: ADD ICON */}
        {'x'} Edit Scoring
      </div>
      {popUpOpen && (
        <UserConfigPopup
          generalScores={generalScores}
          setGeneralScores={setGeneralScores}
          fileScores={fileScores}
          setFileScores={setFileScores}
          togglePopup={togglePopup}
        />
      )}
      <SaveUserConfig setCurrentConfig={setCurrentConfig} />
    </div>
  )
}

export default UserConfig
