import { useState } from 'react'
import classNames from '../utils/classNames'

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
      <button className={styles.toggleButton} onClick={toggleComment}>
        <Dropdown className={isOpen ? styles.openIcon : styles.closedIcon} />
      </button>
      <div
        className={classNames(
          styles.commentContainer,
          isOpen ? styles.expanded : styles.collapsed
        )}
      >
        {comment}
      </div>
    </div>
  )
}

export default CommentAccordion
