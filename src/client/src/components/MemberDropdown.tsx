import { Select, FormControl, InputLabel, MenuItem } from '@material-ui/core'

import { TMemberData } from '../types'

import styles from '../css/MemberDropdown.module.css'

export interface IMemberDropdownProps {
  data: TMemberData
  defaultSelected: string
  committerEmail: string
}

const MemberDropdown = ({
  data,
  defaultSelected,
  committerEmail,
}: IMemberDropdownProps) => {
  return (
    <FormControl variant="outlined" style={{ minWidth: 300 }}>
      <Select
        autoWidth={false}
        defaultValue={defaultSelected}
        id={committerEmail}
      >
        {data?.map(({ displayName, id, username }) => {
          return (
            <MenuItem key={id} value={id}>
              {displayName} @{username}
            </MenuItem>
          )
        })}
        <MenuItem value="ignore">Ignore committer</MenuItem>
      </Select>
    </FormControl>
  )
}

export default MemberDropdown
