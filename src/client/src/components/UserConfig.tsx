import {
  FormEventHandler,
  InputHTMLAttributes,
  useContext,
  useEffect,
  useState,
} from 'react'
import {
  ADD_CONFIG,
  FLUSH_CONFIGS,
  SET_END_DATE,
  SET_GRAPH_BY,
  SET_GRAPH_Y_AXIS,
  SET_SCORE_BY,
  SET_START_DATE,
  UPDATE_CONFIG,
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
  const [popUpOpen, setPopUpOpen] = useState(false)
  const [name, setName] = useState(userConfigs.selected.name)

  const save: FormEventHandler = event => {
    event.preventDefault()
    const [foundConfig] = Object.values(userConfigs.configs).filter(
      c => c.name === name
    )
    if (foundConfig?.id) {
      dispatch({ type: UPDATE_CONFIG, id: foundConfig.id })
    } else {
      dispatch({ type: ADD_CONFIG, name })
    }
    setName('')
  }

  const togglePopup = () => {
    setPopUpOpen(!popUpOpen)
  }

  useEffect(() => {
    dispatch({ type: FLUSH_CONFIGS })
  }, [])

  return (
    <>
      <SideNavItem label="Settings" Icon={settingsIcon}>
        <SideNavSubItem startOpened label="Date Range">
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
        <SideNavSubItem startOpened label="Member scores by">
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
        <SideNavSubItem startOpened label="Graph Settings">
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
        {popUpOpen && <UserConfigPopup togglePopup={togglePopup} />}
        <form className={styles.saveContainer} onSubmit={save}>
          <div className={styles.saveLabel}>
            <SaveLarge className={styles.saveIcon} /> Save Configuration
          </div>
          <input
            value={name}
            onChange={e => setName(e.target.value)}
            className={styles.nameInput}
            placeholder="Name config..."
          />
          <button type="submit" className={styles.saveButton} disabled={!name}>
            <SaveSmall className={styles.saveIcon} /> Save Config
          </button>
        </form>
      </SideNavItem>
      <SideNavItem Icon={toolIcon} label="Saved Configs">
        <SavedConfigs />
      </SideNavItem>
    </>
  )
}

export default UserConfig
