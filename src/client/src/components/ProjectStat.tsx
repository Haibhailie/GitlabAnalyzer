import styles from '../css/ProjectStat.module.css'

export interface IProjectStatProps {
  value: string | number
  name: string
}

const ProjectStat = ({ name, value }: IProjectStatProps) => {
  return (
    <div className={styles.container}>
      <p className={styles.name}>{name}</p>
      <p className={styles.value}>{value}</p>
    </div>
  )
}

export default ProjectStat
