import { Select, FormControl, InputLabel, MenuItem } from '@material-ui/core'
import styles from '../css/MemberDropdown.module.css'

export interface IDropdownProps {
  data: Record<string, any>[]
}

const MemberDropdown = ({ data }: IDropdownProps) => {
  return (
    // <select className={styles.dropdown}>
    //   <option className={styles.option} value="Ali Khamesy">
    //     Ali Khamesy @akhamesy
    //   </option>
    //   <option className={styles.option} value="Grace Luo">
    //     Grace Luo @gla74
    //   </option>
    // </select>
    <FormControl variant="outlined" style={{ minWidth: 300 }}>
      <Select autoWidth={false}>
        <MenuItem value={'Ali Khamesy'} selected>
          Ali Khamesy @akhamesy
        </MenuItem>
        <MenuItem value={'Grace Luo'} selected>
          Grace Luo @gla74
        </MenuItem>
      </Select>
    </FormControl>
  )
}

export default MemberDropdown
