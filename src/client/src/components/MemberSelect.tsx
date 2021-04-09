import { TMemberData } from '../types'

import styles from '../css/MemberSelect.module.css'

export interface IMemberSelectProps {
  data: TMemberData
  defaultSelected: string
  committerEmail: string
  onChange: (event: React.ChangeEvent<HTMLSelectElement>) => void
}

const MemberSelect = ({
  data,
  defaultSelected,
  committerEmail,
  onChange,
}: IMemberSelectProps) => {
  return (
    <select
      name={committerEmail}
      defaultValue={defaultSelected}
      className={styles.select}
      onChange={onChange}
    >
      <option value="">Ignore committer</option>
      {data?.map(({ displayName, id, username }) => {
        return (
          id && (
            <option key={username} value={id}>
              {displayName} @{username}
            </option>
          )
        )
      })}
    </select>
  )
}

export default MemberSelect
