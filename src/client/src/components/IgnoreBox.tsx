import { MouseEventHandler, MouseEvent, HTMLProps } from 'react'

import styles from '../css/IgnoreBox.module.css'
import classNames from '../utils/classNames'

const eventStopper = (onClick?: (event: MouseEvent) => void) => {
  const handler: MouseEventHandler = event => {
    event.stopPropagation()
    onClick?.(event)
  }
  return handler
}

const IgnoreBox = ({
  onClick,
  className,
  ...props
}: HTMLProps<HTMLInputElement>) => (
  <input
    {...props}
    onClick={eventStopper(e => onClick?.(e as MouseEvent<HTMLInputElement>))}
    type="checkbox"
    className={classNames(styles.ignore, className)}
  />
)

export default IgnoreBox
