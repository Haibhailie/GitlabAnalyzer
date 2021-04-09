import { TCommitDiffs, TFileData, TLineType } from '../types'

import Dropdown from './Dropdown'

import styles from '../css/Diff.module.css'

export interface IDiffProps {
  data?: TFileData
  type: 'MR' | 'Commit'
  id: string
  commits?: TCommitDiffs
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

const Diff = ({ data, type, id, commits }: IDiffProps) => {
  console.log(data)

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          {type} {id}
        </div>
        <div>
          {type} score: {data?.[0].fileScore.scoreAdditions}
        </div>
        {commits && (
          <div>Commit score: {commits[0].fileScore.scoreAdditions}</div>
        )}
      </div>
      <div className={styles.scrollContainer}>
        <div className={styles.files}>
          {data?.map(({ name, fileScore, linesOfCodeChanges, fileDiffs }) => (
            <Dropdown
              key={name}
              header={
                <div className={styles.fileHeader}>
                  <span>{name}</span>
                  <span>{fileScore.totalScore}</span>
                  <span>{linesOfCodeChanges.numAdditions}</span>
                  <span>{linesOfCodeChanges.numDeletions}</span>
                </div>
              }
              arrowOnLeft={true}
            >
              <div>
                {fileDiffs.map(
                  ({ lineType, diffLine }) =>
                    lineType !== 'HEADER' &&
                    lineType !== 'LINE_NUMBER_SPECIFICATION' && (
                      <div className={getLineClassName(lineType)}>
                        {diffLine}
                      </div>
                    )
                )}
              </div>
            </Dropdown>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Diff
