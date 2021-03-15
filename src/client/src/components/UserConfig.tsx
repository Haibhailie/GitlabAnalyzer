import { ChangeEvent, InputHTMLAttributes, useContext, useState } from 'react'
import {
  SET_END_DATE,
  SET_GRAPH_BY,
  SET_GRAPH_Y_AXIS,
  SET_SCORE_BY,
  SET_START_DATE,
  UserConfigContext,
} from '../context/UserConfigContext'

import MuiDatePicker from '@material-ui/lab/DatePicker'
import TextField from '@material-ui/core/TextField'
import SideNavSubItem from './SideNavSubItem'
import UserConfigPopup from './UserConfigPopup'
import SavedConfigs from './SavedConfigs'
import SideNavItem from './SideNavItem'

import styles from '../css/UserConfig.module.css'

import { ReactComponent as SaveLarge } from '../assets/save-large.svg'
import { ReactComponent as SaveSmall } from '../assets/save-small.svg'
import { ReactComponent as Edit } from '../assets/edit.svg'
import { ReactComponent as toolIcon } from '../assets/tool.svg'
import { ReactComponent as settingsIcon } from '../assets/settings.svg'

const DatePicker = (props: {
  onChange: (value: Date | null) => void
  value: Date | null
  label: string
}) => (
  <MuiDatePicker
    {...props}
    renderInput={params => (
      <TextField
        {...params}
        margin="dense"
        size="small"
        helperText={null}
        className={styles.dateContainer}
      />
    )}
  />
)

interface IRadioInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

const RadioInput = ({ label, value, ...props }: IRadioInputProps) => (
  <div className={styles.inputField}>
    <input {...props} type="radio" defaultValue={value ?? ''} />
    <label className={styles.label}>{label}</label>
  </div>
)

const UserConfig = () => {
  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const [fileScores, setFileScores] = useState(userConfigs.selected.fileScores)
  const [generalScores, setGeneralScores] = useState(
    userConfigs.selected.generalScores
  )

  const [popUpOpen, setPopUpOpen] = useState(false)

  const [name, setName] = useState(userConfigs.selected.name)

  const nameChange = (event: ChangeEvent<HTMLInputElement>) => {
    const newName = event.target.value
    setName(newName)
    // if (checkUniqueName(newName)) {
    //   dispatch({ type: 'SET_CONFIG_NAME', name: newName })
    // }
  }

  const save = () => {
    // const newSavedConfigs = savedConfigs
    // newSavedConfigs.push(userConfigs.selected)
    // setSavedConfigs([...newSavedConfigs])
    setName('')
  }

  const togglePopup = () => {
    setPopUpOpen(!popUpOpen)
  }

  return (
    <>
      <SideNavItem label="Settings" Icon={settingsIcon}>
        <SideNavSubItem startOpened={true} label="Date Range">
          <DatePicker
            label="Start date"
            onChange={d => d && dispatch({ type: SET_START_DATE, date: d })}
            value={userConfigs.selected.startDate ?? null}
          />
          <DatePicker
            label="End date"
            onChange={date => date && dispatch({ type: SET_END_DATE, date })}
            value={userConfigs.selected.endDate ?? null}
          />
        </SideNavSubItem>
        <SideNavSubItem startOpened={true} label="Member scores by">
          <RadioInput
            label="Merge Requests"
            name="SCORE"
            checked={userConfigs.selected.scoreBy === 'MRS'}
            onChange={() => dispatch({ type: SET_SCORE_BY, scoreBy: 'MRS' })}
          />
          <RadioInput
            label="Commits"
            name="SCORE"
            checked={userConfigs.selected.scoreBy === 'COMMITS'}
            onChange={() =>
              dispatch({ type: SET_SCORE_BY, scoreBy: 'COMMITS' })
            }
          />
        </SideNavSubItem>
        <SideNavSubItem startOpened={true} label="Graph Settings">
          <p className={styles.subHeader}>Graph Y-Axis</p>
          <RadioInput
            label="Number"
            name="yAxis"
            checked={userConfigs.selected.yAxis === 'NUMBER'}
            onChange={() =>
              dispatch({ type: SET_GRAPH_Y_AXIS, yAxis: 'NUMBER' })
            }
          />
          <RadioInput
            label="Score"
            name="yAxis"
            checked={userConfigs.selected.yAxis === 'SCORE'}
            onChange={() =>
              dispatch({ type: SET_GRAPH_Y_AXIS, yAxis: 'SCORE' })
            }
          />
          <p className={styles.subHeader}>Project Graph</p>
          <RadioInput
            label="Entire Project"
            name="Graph"
            checked={userConfigs.selected.graphMode === 'PROJECT'}
            onChange={() =>
              dispatch({ type: SET_GRAPH_BY, graphMode: 'PROJECT' })
            }
          />
          <RadioInput
            label="Split By Member"
            name="Graph"
            checked={userConfigs.selected.graphMode === 'MEMBER'}
            onChange={() =>
              dispatch({ type: SET_GRAPH_BY, graphMode: 'MEMBER' })
            }
          />
        </SideNavSubItem>
        <SideNavSubItem
          onClick={togglePopup}
          label="Edit Scoring"
          Icon={Edit}
        />
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
          <button className={styles.saveButton} onClick={save} disabled={!name}>
            <SaveSmall className={styles.saveIcon} /> Save Config
          </button>
        </div>
      </SideNavItem>
      <SideNavItem Icon={toolIcon} label="Saved Configs">
        <SavedConfigs />
      </SideNavItem>
    </>
  )
}

export default UserConfig
