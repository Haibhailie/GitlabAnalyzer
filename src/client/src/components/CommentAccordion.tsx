import { useState } from 'react'
import styles from '../css/CommentAccordion.module.css'

import { ReactComponent as Dropdown } from '../assets/dropdown-small.svg'

export interface ICommentAccordionProps {
  comment: string
}

const CommentAccordion = ({ comment }: ICommentAccordionProps) => {
  const [isOpen, setIsOpen] = useState(false)

  const toggleComment = () => {
    setIsOpen(!isOpen)
  }

  return (
    <div className={styles.container}>
      <button onClick={toggleComment}>
        <Dropdown className={isOpen ? styles.openIcon : styles.closedIcon} />
      </button>
      <div className={styles.commentContainer}>{comment}</div>
    </div>
  )
}

export default CommentAccordion
