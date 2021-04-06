import classNames from '../utils/classNames'

import styles from '../css/AnalyzeButton.module.css'

export interface IAnalyzeButtonProps {
  id: string
  message: string
  index?: number
  onClick: (id: string) => void
  className?: string
  disabled?: boolean
  isAnalyzing: boolean | undefined
  Icon: React.FunctionComponent<React.SVGProps<SVGSVGElement>>
}

const AnalyzeButton = ({
  id,
  message,
  onClick,
  className,
  disabled,
  isAnalyzing,
  Icon,
}: IAnalyzeButtonProps) => {
  return (
    <button
      className={classNames(
        styles.analyze,
        className,
        isAnalyzing && styles.loading
      )}
      onClick={event => {
        event.stopPropagation()
        onClick(id)
      }}
      disabled={disabled}
    >
      {message}
      <Icon className={styles.icon} />
    </button>
  )
}

export default AnalyzeButton
