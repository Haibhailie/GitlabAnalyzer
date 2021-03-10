import React, { useContext, useState } from 'react'
import { IUserConfig, UserConfigContext } from '../context/UserConfigContext'
import TextField from '@material-ui/core/TextField'

import styles from '../css/UserConfig.module.css'
import SideNavSubDropDown from './SideNavSubDropDown'

const UserConfig = () => {
  const { userConfig, dispatch } = useContext(UserConfigContext)

  const [startDate, setStartDate] = useState(userConfig.startDate)
  const [endDate, setEndDate] = useState(userConfig.endDate)
  const [scoreBy, setScoreBy] = useState(userConfig.scoreBy)
  const [graphYAxis, setgraphYAxis] = useState(userConfig.graphYAxis)
  const [projectGraphBy, setProjectGraphBy] = useState(
    userConfig.projectGraphBy
  )

  const [dateError, setDateError] = useState(false)
  const startDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    setStartDate(newValue)
    if (validDates(newValue, endDate)) {
      dispatch({
        type: 'SET_START_DATE',
        startDate: newValue,
      })
    }
  }
  const endDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = new Date(event.target.value)
    setEndDate(newValue)
    if (validDates(startDate, newValue)) {
      dispatch({
        type: 'SET_END_DATE',
        endDate: newValue,
      })
    }
  }

  const validDates = (start?: Date, end?: Date) => {
    console.log(start, end)
    if (start && end && start > end) {
      setDateError(true)
      return false
    } else {
      setDateError(false)
      return true
    }
  }

  const memberScoreByChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value as IUserConfig['scoreBy']
    setScoreBy(newValue)
    dispatch({
      type: 'SET_SCORE_BY',
      scoreBy: newValue,
    })
  }

  const graphYAxisChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value as IUserConfig['graphYAxis']
    setgraphYAxis(newValue)
    dispatch({
      type: 'SET_GRAPH_Y_AXIS',
      graphYAxis: newValue,
    })
  }

  const projectGraphByChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value as IUserConfig['projectGraphBy']
    setProjectGraphBy(newValue)
    dispatch({
      type: 'SET_PROJECT_GRAPH_BY',
      projectGraphBy: newValue,
    })
  }

  const formatDate = (date: Date) => {
    if (date) {
      return date.toISOString().split('T')[0]
    } else {
      return
    }
  }

  return (
    <div>
      <form>
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
                value={startDate ? formatDate(startDate) : ''}
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
                value={endDate ? formatDate(endDate) : ''}
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
                checked={scoreBy === 'Merge Requests'}
                onChange={memberScoreByChange}
              />
              <label className={styles.label}>Merge Requests</label>
            </div>
            <div className={styles.inputField}>
              <input
                type="radio"
                value="Commits"
                checked={scoreBy === 'Commits'}
                onChange={memberScoreByChange}
              />
              <label className={styles.label}>Commits</label>
            </div>
          </>
        </SideNavSubDropDown>
        <SideNavSubDropDown initial={true} label="Graph Settings">
          <div>
            <p className={styles.subHeader}>Graph Y-Axis</p>
            <div className={styles.inputField}>
              <input
                type="radio"
                value="Number"
                checked={graphYAxis === 'Number'}
                onChange={graphYAxisChange}
              />
              <label className={styles.label}>Number</label>
            </div>
            <div className={styles.inputField}>
              <input
                type="radio"
                value="Score"
                checked={graphYAxis === 'Score'}
                onChange={graphYAxisChange}
              />
              <label className={styles.label}>Score</label>
            </div>
            <p className={styles.subHeader}>Project Graph</p>
            <div className={styles.inputField}>
              <input
                type="radio"
                value="Entire Project"
                checked={projectGraphBy === 'Entire Project'}
                onChange={projectGraphByChange}
              />
              <label className={styles.label}>Entire Project</label>
            </div>
            <div className={styles.inputField}>
              <input
                type="radio"
                value="Split By Member"
                checked={projectGraphBy === 'Split By Member'}
                onChange={projectGraphByChange}
              />
              <label className={styles.label}>Split By Member</label>
            </div>
          </div>
        </SideNavSubDropDown>
      </form>
    </div>
  )
}

export default UserConfig
