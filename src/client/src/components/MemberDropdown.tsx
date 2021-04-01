import { Select, FormControl, InputLabel, MenuItem } from '@material-ui/core'

import { TMemberData } from '../types'

import styles from '../css/MemberDropdown.module.css'

export interface IMemberDropdownProps {
  data: TMemberData
  selected: string
}

const MemberDropdown = ({ data, selected }: IMemberDropdownProps) => {
  return (
    <FormControl variant="outlined" style={{ minWidth: 300 }}>
      <Select autoWidth={false} defaultValue={selected}>
        {data?.map(({ displayName, id, username }) => {
          return (
            <MenuItem key={id} value={id}>
              {displayName} @{username}
            </MenuItem>
          )
        })}
        <MenuItem value="-1">Ignore committer</MenuItem>
      </Select>
    </FormControl>
  )
}

export default MemberDropdown
