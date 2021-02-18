import { useState } from 'react'
import classNames from '../utils/classNames'

import styles from '../css/Table.module.css'

export interface ITableProps {
  classes?: { table?: string; header?: string; data?: string }
  columnWidths?: string[]
  data: Record<string, any>[]
  headers?: string[]
  excludeHeaders?: boolean
  sortable?: boolean
}

const Table = ({
  sortable,
  classes,
  data,
  headers,
  columnWidths,
  excludeHeaders,
}: ITableProps) => {
  const [sortConfig, setSortConfig] = useState({ by: '', asc: true })

  if (typeof data !== 'object') return null

  const sortKeys = Object.keys(data[0])

  const sortDataBy = (columnIndex: number) => {
    const columnToSortBy = sortKeys[columnIndex]
    const asc = sortConfig.by !== columnToSortBy || !sortConfig.asc

    data.sort((a, b) => {
      if (a[columnToSortBy] < b[columnToSortBy]) {
        return asc ? -1 : 1
      } else if (a[columnToSortBy] > b[columnToSortBy]) {
        return asc ? 1 : -1
      }
      return 0
    })

    setSortConfig({
      by: columnToSortBy,
      asc,
    })
  }

  const dataHeaders = excludeHeaders ? [] : headers ?? sortKeys
  const numColumns = dataHeaders.length
  const gridTemplateColumns = columnWidths
    ? columnWidths.join(' ')
    : `repeat(1fr, ${numColumns})`

  return (
    <div
      style={{ gridTemplateColumns }}
      className={classNames(styles.table, classes?.table)}
    >
      {dataHeaders.map((header, i) => (
        <div
          key={header}
          className={classNames(styles.header, classes?.header)}
        >
          {sortable ? (
            <button className={styles.sortBtn} onClick={() => sortDataBy(i)}>
              {header}
            </button>
          ) : (
            header
          )}
        </div>
      ))}
      {data.map(row =>
        Object.entries(row).map(([heading, cell]) => (
          <div key={heading} className={classNames(styles.data, classes?.data)}>
            {cell}
          </div>
        ))
      )}
    </div>
  )
}

export default Table
