import { useState } from 'react'
import classNames from '../utils/classNames'

import styles from '../css/Table.module.css'
import Dropdown from './Dropdown'

export interface ITableProps {
  classes?: {
    container?: string
    table?: string
    header?: string
    data?: string
    title?: string
    row?: string
  }
  columnWidths?: string[]
  data: Record<string, any>[]
  headers?: string[]
  excludeHeaders?: boolean
  sortable?: boolean
  title?: string
  collapsible?: boolean
  isOpen?: boolean
  maxHeight?: number
  startOpened?: boolean
  onClick?: (
    event: React.MouseEvent<HTMLDivElement, MouseEvent>,
    index: number
  ) => void
}

const Table = ({
  sortable,
  classes,
  data,
  headers,
  columnWidths,
  excludeHeaders,
  title,
  collapsible,
  isOpen,
  maxHeight,
  startOpened,
  onClick,
}: ITableProps) => {
  const [sortConfig, setSortConfig] = useState({ by: '', asc: true })

  if (typeof data !== 'object') return null

  const sortKeys = Object.keys(data[0] ?? {})

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

  const dataHeaders = headers ?? sortKeys
  const numColumns = dataHeaders.length
  const gridTemplateColumns = columnWidths
    ? columnWidths.join(' ')
    : `repeat(${numColumns}, 1fr)`

  return (
    <Dropdown
      isOpen={!collapsible || isOpen}
      className={classNames(styles.container, classes?.container)}
      classes={{ dropdown: styles.dropdown }}
      header={
        title && (
          <div className={classNames(styles.title, classes?.title)}>
            {title}
          </div>
        )
      }
      fixedCollapsed={!collapsible}
      maxHeight={maxHeight}
      startOpened={startOpened}
    >
      <div
        style={{
          gridTemplateColumns,
        }}
        className={classNames(styles.table, classes?.table)}
      >
        {!excludeHeaders && (
          <div className={styles.row}>
            {dataHeaders.map((header, i) => (
              <div
                key={header}
                className={classNames(styles.header, classes?.header)}
              >
                {sortable ? (
                  <button
                    className={styles.sortBtn}
                    onClick={() => sortDataBy(i)}
                  >
                    {header}
                  </button>
                ) : (
                  header
                )}
              </div>
            ))}
          </div>
        )}
        {data.map((row, index) => (
          <div
            key={index}
            className={classNames(styles.row, classes?.row)}
            onClick={e => onClick?.(e, index)}
          >
            {Object.entries(row).map(([heading, cell]) => (
              <div
                key={heading}
                className={classNames(styles.data, classes?.data)}
              >
                {cell}
              </div>
            ))}
          </div>
        ))}
      </div>
    </Dropdown>
  )
}

export default Table
