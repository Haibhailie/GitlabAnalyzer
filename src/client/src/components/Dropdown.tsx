import { ReactNode, useEffect, useState, useRef } from 'react'
import { MAX_INT } from '../utils/constants'
import classNames from '../utils/classNames'

import styles from '../css/Dropdown.module.css'

import { ReactComponent as Gt } from '../assets/greater-than.svg'

export interface IDropdownProps {
  children: ReactNode
  header: ReactNode
  isOpen?: boolean
  startOpened?: boolean
  fixedCollapsed?: boolean
}

const Dropdown = ({
  header,
  children,
  isOpen: openOverride,
  startOpened,
  fixedCollapsed,
}: IDropdownProps) => {
  const [isOpen, setOpen] = useState(!!startOpened)
  const [height, setHeight] = useState(MAX_INT)
  const [firstPass, setFirstPass] = useState(true)
  const dropdownRef = useRef<HTMLDivElement>(null)

  useEffect(() => setOpen(!!openOverride), [openOverride])

  useEffect(() => {
    setHeight(dropdownRef.current?.clientHeight ?? MAX_INT)
    setFirstPass(false)
  }, [])

  const toggleCollapse = () => setOpen(!isOpen)

  return (
    <div
      className={classNames(styles.container, !isOpen && styles.collapsed)}
      onClick={toggleCollapse}
    >
      {header && (
        <button className={styles.headerBtn}>
          <div className={styles.header}>
            {header}
            {!fixedCollapsed && <Gt className={styles.collapseImg} />}
          </div>
        </button>
      )}
      <div
        className={classNames(styles.dropdown, firstPass && styles.firstPass)}
        style={{
          maxHeight: `${firstPass || isOpen ? height : 0}px`,
        }}
        ref={dropdownRef}
      >
        {children}
      </div>
    </div>
  )
}

export default Dropdown
