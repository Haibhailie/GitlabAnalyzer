import { useState } from 'react'

import styles from '../css/Selector.module.css'
import classNames from '../utils/classNames'

export interface ISelectorProps {
  children: JSX.Element[]
  tabHeaders: string[]
  defaultTabHeader?: number
  className?: string
}

const Selector = ({
  children,
  tabHeaders,
  defaultTabHeader,
  className,
}: ISelectorProps) => {
  const [selected, setSelected] = useState(defaultTabHeader ?? 0)

  if (tabHeaders.length != children.length) {
    console.error(
      `Size of children (${children.length}) does not match the size of tab headers (${tabHeaders.length})`
    )
    return null
  }

  return (
    <>
      <div className={classNames(styles.selectorTabHeaders, className)}>
        {tabHeaders.map((tabHeader, index) => (
          <button
            className={
              index === selected
                ? styles.activeTabHeader
                : styles.inactiveTabHeader
            }
            key={tabHeader}
            onClick={() => setSelected(index)}
          >
            {tabHeader}
          </button>
        ))}
      </div>
      {children.map((child, i) => (
        <div
          key={i}
          style={{
            ...(i !== selected && {
              visibility: 'hidden',
              position: 'absolute',
              overflow: 'hidden',
              zIndex: -1000,
              left: 0,
              top: 0,
            }),
            width: '100%',
            height: '100%',
          }}
        >
          {child}
        </div>
      ))}
    </>
  )
}

export default Selector
