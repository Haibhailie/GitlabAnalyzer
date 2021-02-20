import { useState } from 'react'

import styles from '../css/Selector.module.css'

export interface ISelectorProps {
  children: JSX.Element[]
  headers: string[]
  defaultHeader?: number
}

const Selector = ({ children, headers, defaultHeader }: ISelectorProps) => {
  const [selected, setSelected] = useState(defaultHeader ?? 0)

  if (headers.length != children.length) {
    console.error(
      `Size of children (${children.length}) does not match the size of headers (${headers.length})`
    )
    return null
  }

  return (
    <>
      <div className={styles.selectorHeaders}>
        {headers.map((header, index) => (
          <button
            className={
              index === selected ? styles.activeTab : styles.inactiveTab
            }
            key={header}
            onClick={() => setSelected(index)}
          >
            {header}
          </button>
        ))}
      </div>
      {children[selected]}
    </>
  )
}

export default Selector
