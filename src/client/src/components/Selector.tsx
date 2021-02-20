import { useState } from 'react'

import styles from '../css/Selector.module.css'

export interface ISelectorProps {
  children: JSX.Element[]
  tabHeaders: string[]
  defaultTabHeader?: number
}

const Selector = ({
  children,
  tabHeaders,
  defaultTabHeader,
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
      <div className={styles.selectorTabHeaders}>
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
      {children[selected]}
    </>
  )
}

export default Selector
