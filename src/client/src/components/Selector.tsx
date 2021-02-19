import { useState } from 'react'

import styles from '../css/Selector.module.css'

export interface ISelectorProps {
  children: JSX.Element | JSX.Element[]
  headers: string[]
}

const Selector = ({ children, headers }: ISelectorProps) => {
  const [selected, setSelected] = useState(0)

  if (!(children instanceof Array)) {
    children = [children]
  }

  if (headers.length != children.length) {
    return null
  }

  return (
    <div>
      <div className={styles.selectorHeaders}>
        {headers.map((header, index) => (
          <button key={index} onClick={() => setSelected(index)}>
            {header}
          </button>
        ))}
      </div>
      <div className={styles.selectorBody}>{children[selected]}</div>
    </div>
  )
}

export default Selector

/**
 *<Selector headers={['A', 'B']}>
      <form>
        <label>A</label>
        <input type="text" />
        <input type="password" />
      </form>
      <form>
        <label>B</label>
        <input type="text" />
        <input type="password" />
      </form>
    </Selector>
 */
