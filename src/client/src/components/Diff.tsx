import { TCommitDiffs, TFileData, TLineType } from '../types'
import { LONG_STRING_LEN } from '../utils/constants'

import Dropdown from './Dropdown'
import IgnoreBox from './IgnoreBox'

import styles from '../css/Diff.module.css'

export interface IDiffProps {
  data?: TFileData
  type: 'MR' | 'Commit'
  id: string
  commits?: TCommitDiffs
  title: string
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

const Diff = ({ data, type, id, commits, title }: IDiffProps) => {
  // TODO: Get score data from context or get that in MergeRequests and pass in. Use it in header.
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.stats}>
          <div>
            {type} {id}
          </div>
          <div>
            {type} score: {data?.[0].fileScore.totalScore}
          </div>
          {commits && (
            <div>Commit score: {commits[0].fileScore.totalScore}</div>
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
      {data?.map(({ name, fileScore, linesOfCodeChanges, fileDiffs }) => (
        <Dropdown
          // TODO: use fileId instead of name for key.
          key={name}
          className={styles.file}
          arrowOnLeft
          startOpened
          header={
            <div className={styles.fileHeader}>
              <span className={styles.fileName}>{name}</span>
              <div className={styles.fileScore}>
                <span className={styles.ignore}>
                  Ignore: <IgnoreBox className={styles.ignoreBox} />
                </span>
                <span className={styles.score}>
                  {/* TODO: Score breakdown tooltip*/}
                  score: {fileScore.totalScore.toFixed(1)}
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
                    <div className={getLineClassName(lineType)}>{diffLine}</div>
                  )
              )}
            </div>
          </div>
        </Dropdown>
      ))}
    </div>
  )
}

export default Diff
