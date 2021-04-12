import { TLineType } from '../types'
import { LONG_STRING_LEN } from '../utils/constants'
import { IFile } from '../context/ProjectContext'
import { Tooltip } from '@material-ui/core'

import Dropdown from './Dropdown'
import IgnoreBox from './IgnoreBox'

import styles from '../css/Diff.module.css'

import info from '../assets/info.svg'

export interface IDiffProps {
  data?: IFile[]
  type: 'MR' | 'Commit'
  score: number
  id: string
  commitsScore?: number
  title: string
  ignore: (fileId: string, setIgnored: boolean) => void
}

const getLineClassName = (lineType: TLineType) => {
  if (lineType.startsWith('ADDITION')) {
    return `${styles.line} ${styles.addition}`
  } else if (lineType.startsWith('DELETION')) {
    return `${styles.line} ${styles.deletion}`
  } else {
    return `${styles.line} ${styles.unchanged}`
  }
}

const Diff = ({
  data,
  type,
  id,
  commitsScore,
  score,
  title,
  ignore,
}: IDiffProps) => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.stats}>
          <div>
            {type} {id}
          </div>
          <div>
            {type} score: {score.toFixed(1)}
          </div>
          {type === 'MR' && commitsScore && (
            <div>Commit score: {commitsScore.toFixed(1)}</div>
          )}
        </div>
        <Dropdown
          fixedCollapsed={title.length < LONG_STRING_LEN}
          className={styles.message}
          header={<div className={styles.messageHeader}>{title}</div>}
        >
          <div className={styles.title}>{title}</div>
        </Dropdown>
      </div>
      {data?.map(
        ({
          name,
          score,
          linesOfCodeChanges,
          fileDiffs,
          fileId,
          isIgnored,
          scores,
        }) => (
          <Dropdown
            key={fileId}
            className={styles.file}
            arrowOnLeft
            startOpened
            header={
              <div className={styles.fileHeader}>
                <span className={styles.fileName}>{name}</span>
                <div className={styles.fileScore}>
                  <span className={styles.ignore}>
                    Ignore:{' '}
                    <IgnoreBox
                      onChange={e => {
                        const checked = (e.target as HTMLInputElement).checked
                        console.log(checked)
                        ignore(fileId, checked)
                      }}
                      checked={isIgnored}
                      className={styles.ignoreBox}
                    />
                  </span>
                  <span className={styles.score}>
                    score: {score.toFixed(1)}
                    <Tooltip
                      title={
                        <div className={styles.breakdown}>
                          <div>additions: +{scores.additions.toFixed(1)} </div>
                          <div>comments: +{scores.comments.toFixed(1)} </div>
                          <div>
                            whitespaces: +{scores.whitespaces.toFixed(1)}{' '}
                          </div>
                          <div>syntax: +{scores.syntaxes.toFixed(1)} </div>
                          <div>deletions: +{scores.deletions.toFixed(1)} </div>
                        </div>
                      }
                      placement="top"
                      arrow
                    >
                      <img className={styles.icon} src={info} />
                    </Tooltip>
                  </span>
                  <span className={styles.additions}>
                    +{linesOfCodeChanges.numAdditions}
                  </span>
                  <span className={styles.deletions}>
                    -{linesOfCodeChanges.numDeletions}
                  </span>
                </div>
              </div>
            }
          >
            <div className={styles.diffVerticalScroll}>
              <div className={styles.diff}>
                {fileDiffs.map(
                  ({ lineType, diffLine }) =>
                    lineType !== 'HEADER' && (
                      <div className={getLineClassName(lineType)}>
                        {diffLine}
                      </div>
                    )
                )}
              </div>
            </div>
          </Dropdown>
        )
      )}
    </div>
  )
}

export default Diff
