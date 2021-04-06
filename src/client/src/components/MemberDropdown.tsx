import { useEffect, useState } from 'react'
import { useHistory } from 'react-router-dom'
import { IMemberData } from '../types'

import { ReactComponent as Go } from '../assets/go.svg'

import styles from '../css/MemberDropdown.module.css'

export interface IMemberDropdownProp {
  currentMemberId: string
  projectId: string
  members: IMemberData[]
}

const MemberDropdown = ({
  currentMemberId,
  projectId,
  members,
}: IMemberDropdownProp) => {
  const [selectedId, setSelectedId] = useState(currentMemberId)
  const history = useHistory()

  const onMemberChange = () => {
    history.push(`/project/${projectId}/member/${selectedId}`)
  }

  useEffect(() => {
    setSelectedId(currentMemberId)
  }, [currentMemberId])

  return (
    <div className={styles.container}>
      <div className={styles.header}>Select Member:</div>
      <div className={styles.subContainer}>
        <select
          value={selectedId}
          onChange={e => setSelectedId(e.target.value)}
          className={styles.dropdown}
        >
          {members.map(member => (
            <option value={member.id} key={member.id}>
              {member.displayName}
            </option>
          ))}
        </select>
        <button onClick={onMemberChange} className={styles.button}>
          <Go />
        </button>
      </div>
    </div>
  )
}

export default MemberDropdown
