import { Select, FormControl, InputLabel, MenuItem } from '@material-ui/core'
import styles from '../css/MemberDropdown.module.css'

export interface IDropdownProps {
  data: Record<string, any>[]
  selected: string
}

const MemberDropdown = ({ data, selected }: IDropdownProps) => {
  return (
    <FormControl variant="outlined" style={{ minWidth: 300 }}>
      <Select autoWidth={false} defaultValue={selected}>
        <MenuItem value={'Ali Khamesy'}>Ali Khamesy @akhamesy</MenuItem>
        <MenuItem value={'Grace Luo'}>Grace Luo @gla74</MenuItem>
      </Select>
    </FormControl>
  )
}

export default MemberDropdown
