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
  className?: string
  classes?: {
    dropdown?: string
  }
  arrowOnLeft?: boolean
  maxHeight?: number
}

const Dropdown = ({
  header,
  children,
  isOpen: openOverride,
  startOpened,
  fixedCollapsed,
  className,
  classes,
  arrowOnLeft,
  maxHeight,
}: IDropdownProps) => {
  const [isOpen, setOpen] = useState(!!startOpened)

  const [height, setHeight] = useState(MAX_INT)
  const dropdownRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    openOverride && setOpen(!!openOverride)
  }, [openOverride])

  useEffect(() => {
    if (!dropdownRef.current) return

    if (maxHeight) {
      setHeight(maxHeight)
      return
    }

    const fakeChild = dropdownRef.current.cloneNode(true) as HTMLDivElement
    fakeChild.style.maxHeight = ''
    fakeChild.style.width = `${dropdownRef.current.clientWidth}px`
    fakeChild.className = styles.fakeChild
    document.body.appendChild(fakeChild)
    setHeight(fakeChild.clientHeight)
    document.body.removeChild(fakeChild)
  }, [
    children,
    dropdownRef.current?.clientHeight,
    dropdownRef.current?.clientWidth,
  ])

  const toggleCollapse = () => !fixedCollapsed && setOpen(!isOpen)

  return (
    <div className={classNames(className, !isOpen && styles.collapsed)}>
      {header && (
        <button
          className={classNames(
            styles.headerBtn,
            fixedCollapsed && styles.fixedCollapsed
          )}
          onClick={toggleCollapse}
        >
          <div
            className={classNames(
              styles.header,
              arrowOnLeft && styles.leftArrow
            )}
          >
            {header}
            {!fixedCollapsed && (
              <Gt
                className={classNames(
                  styles.collapseImg,
                  classes?.dropdown ?? styles.collapse
                )}
              />
            )}
          </div>
        </button>
      )}
      <div
        className={styles.dropdown}
        style={{
          maxHeight: `${isOpen ? height : 0}px`,
        }}
        ref={dropdownRef}
      >
        {children}
      </div>
    </div>
  )
}

export default Dropdown
