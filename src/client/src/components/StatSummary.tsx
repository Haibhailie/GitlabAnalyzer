import { useState, useEffect, useRef } from 'react'
import { Tooltip } from '@material-ui/core'
import Stat, { IStatProps } from './Stat'

import styles from '../css/StatSummary.module.css'

import clipboard from '../assets/clipboard.svg'

export interface IStatSummaryProps {
  statData: IStatProps[]
}

const StatSummary = ({ statData }: IStatSummaryProps) => {
  const [copied, setCopied] = useState(false)
  const [csvString, setCsvString] = useState('')
  const timeoutRef = useRef<ReturnType<typeof setTimeout>>()

  useEffect(() => {
    setCsvString(
      [
        ['Stat', 'Value'],
        ...statData.map(stat => [stat.name, stat.rawValue ?? stat.value]),
      ]
        .map(r => r.join('\t'))
        .join('\n')
    )
  }, [statData])

  const copyToClipboard = () => {
    //TO-DO: Implement navigator.permissions query for clipboard-read/write
    navigator.clipboard.writeText(csvString)
    setCopied(true)
    timeoutRef.current = setTimeout(() => setCopied(false), 5000)
  }

  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current)
      }
    }
  }, [])

  return (
    <div className={styles.container}>
      {statData?.map(stat => (
        <Stat key={stat.name} {...stat} />
      ))}
      <div className={styles.statTools}>
        <Tooltip title={copied ? 'Copied!' : 'Copy stats'}>
          <button onClick={copyToClipboard} className={styles.copyButton}>
            <img src={clipboard} className={styles.copyIcon} />
          </button>
        </Tooltip>
      </div>
    </div>
  )
}

export default StatSummary
