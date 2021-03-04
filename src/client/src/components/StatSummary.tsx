import styles from '../css/StatSummary.module.css'
import Stat from './Stat'
import { IStatProps } from './Stat'

export interface IStatSummaryProps {
  statData: Array<IStatProps>
}

const StatSummary = ({ statData }: IStatSummaryProps) => {
  console.log(statData)
  return (
    <div className={styles.container}>
      {statData?.map(stat => (
        <Stat key={stat.name} {...stat} />
      ))}
    </div>
  )
}

export default StatSummary
