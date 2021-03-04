import styles from '../css/StatSummary.module.css'
import ProjectStat from './ProjectStat'

export interface IStatData {
  name: string
  value: string | number
  description?: string
}

export interface IStatSummaryProps {
  statData: Array<IStatData>
}

const StatSummary = ({ statData }: IStatSummaryProps) => {
  console.log(statData)
  return (
    <div className={styles.container}>
      {statData.map(stat => (
        <ProjectStat key={stat.name} name={stat.name} value={stat.value} />
      ))}
    </div>
  )
}

export default StatSummary
