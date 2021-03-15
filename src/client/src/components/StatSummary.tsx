import { useState, useEffect, useRef } from 'react'
import { ThemeProvider, Tooltip } from '@material-ui/core'
import Stat, { IStatProps } from './Stat'
import tooltipTheme from '../themes/tooltipTheme'

import styles from '../css/StatSummary.module.css'

import clipboard from '../assets/clipboard.svg'

export interface IStatSummaryProps {
  statData?: IStatProps[]
}

const StatSummary = ({ statData }: IStatSummaryProps) => {
  const [copyMessage, setCopyMessage] = useState('Copy stats')
  const [csvString, setCsvString] = useState('')
  const timeoutRef = useRef<NodeJS.Timeout>()

  useEffect(() => {
    if (statData !== undefined) {
      setCsvString(
        [
          ['Stat', 'Value'],
          ...statData.map(stat => [stat.name, stat.rawValue ?? stat.value]),
        ]
          .map(r => r.join('\t'))
          .join('\n')
      )
    }
  }, [statData])

  const copyToClipboard = () => {
    //TODO: Implement navigator.permissions query for clipboard-read/write
    navigator.clipboard
      .writeText(csvString)
      .then(() => {
        setCopyMessage('Copied!')
        timeoutRef.current = setTimeout(
          () => setCopyMessage('Copy stats'),
          5000
        )
      })
      .catch(() => {
        setCopyMessage('Failed to copy')
        timeoutRef.current = setTimeout(
          () => setCopyMessage('Copy stats'),
          5000
        )
      })
  }

  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current)
      }
    }
  }, [])

  return (
    <ThemeProvider theme={tooltipTheme}>
      <div className={styles.container}>
        {statData?.map(stat => (
          <Stat key={stat.name} {...stat} />
        ))}
        <div className={styles.statTools}>
          <Tooltip title={copyMessage} arrow>
            <button onClick={copyToClipboard} className={styles.copyButton}>
              <img src={clipboard} className={styles.copyIcon} />
            </button>
          </Tooltip>
        </div>
      </div>
    </ThemeProvider>
  )
}

export default StatSummary
