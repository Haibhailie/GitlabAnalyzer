import classNames from '../utils/classNames'

import styles from '../css/AnalyzeButton.module.css'

import gt from '../assets/greater-than.svg'

export interface IAnalyzeButtonProps {
  id: string
  message: string
  onClick: (id: string) => void
  className?: string
}

const AnalyzeButton = ({
  id,
  message,
  onClick,
  className,
}: IAnalyzeButtonProps) => {
  return (
    <button
      className={classNames(styles.analyze, className)}
      onClick={() => onClick(id)}
    >
      {message}
      <img src={gt} />
    </button>
  )
}

export default AnalyzeButton
