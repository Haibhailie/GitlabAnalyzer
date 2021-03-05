import { useState } from 'react'
import Stat, { IStatProps } from './Stat'
import { Tooltip } from '@material-ui/core'

import styles from '../css/StatSummary.module.css'

import clipboard from '../assets/clipboard.svg'

export interface IStatSummaryProps {
  statData: Array<IStatProps>
}

const StatSummary = ({ statData }: IStatSummaryProps) => {
  const [copied, setCopied] = useState(false)
  const csvString = [
    ['Statistic', 'Value'],
    ...statData.map(stat => [stat.name, stat.value]),
  ]
    .map(r => r.join(','))
    .join('\n')

  const copyToClipboard = () => {
    const dummyText = document.createElement('textarea')
    dummyText.value = csvString
    console.log(dummyText.value)
    document.body.appendChild(dummyText)
    dummyText.select()
    document.execCommand('copy')
    document.body.removeChild(dummyText)
    setCopied(true)
    setTimeout(() => setCopied(false), 5000)
  }

  return (
    <div className={styles.container}>
      {statData?.map(stat => (
        <Stat key={stat.name} {...stat} />
      ))}
      <div className={styles.copyStats}>
        <Tooltip title={copied ? 'Copied!' : 'Copy stats'}>
          <button className={styles.copyButton} onClick={copyToClipboard}>
            <img src={clipboard} className={styles.copyIcon} />
          </button>
        </Tooltip>
      </div>
    </div>
  )
}

export default StatSummary
