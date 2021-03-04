import { Tooltip } from '@material-ui/core'
import info from '../assets/info.svg'

import styles from '../css/Stat.module.css'

export interface IStatProps {
  name: string
  value: string | number
  description?: string
}

const Stat = ({ name, value, description }: IStatProps) => {
  return (
    <div className={styles.container}>
      <p className={styles.name}>
        {name}
        {description ? (
          <Tooltip title={description} placement="right">
            <img className={styles.icon} src={info} />
          </Tooltip>
        ) : (
          ''
        )}
      </p>

      <p className={styles.value}>{value}</p>
    </div>
  )
}

export default Stat
