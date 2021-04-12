import { ChangeEvent, useContext, useState } from 'react'
import classNames from '../utils/classNames'
import {
  IFileTypeScoring,
  UserConfigContext,
} from '../context/UserConfigContext'

import Selector from './Selector'
import Modal from './Modal'
import Table from './Table'

import { ReactComponent as Delete } from '../assets/delete.svg'
import { ReactComponent as Warning } from '../assets/error-small.svg'

import styles from '../css/UserConfigPopup.module.css'

interface IUserConfigPopup {
  togglePopup: () => void
}

const UserConfigPopup = ({ togglePopup }: IUserConfigPopup) => {
  const { userConfigs, dispatch } = useContext(UserConfigContext)

  const [fileScores, setFileScores] = useState(userConfigs.selected.fileScores)
  const [generalScores, setGeneralScores] = useState(
    userConfigs.selected.generalScores
  )
  const [newFileTypeName, setNewFileTypeName] = useState('')
  const [requireReanalyze, setRequireReanalyze] = useState(false)

  const generalScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    generalScores[index] = {
      type: generalScores[index].type,
      value: +event.target.value,
    }

    setGeneralScores([...generalScores])
  }

  const fileScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const field = event.target.name
    fileScores[index] = {
      ...fileScores[index],
      [field]:
        field === 'scoreMultiplier' ? +event.target.value : event.target.value,
    }

    setFileScores([...fileScores])

    if (field !== 'scoreMultiplier') {
      console.log(field)
      setRequireReanalyze(true)
    }
  }

  const deleteFileType = (index: number) => {
    fileScores.splice(index, 1)
    setFileScores([...fileScores])
    setRequireReanalyze(true)
  }

  const isValidFileType = () => {
    if (
      newFileTypeName === '' ||
      fileScores.map(score => score.fileExtension).includes(newFileTypeName)
    ) {
      return false
    } else {
      return true
    }
  }

  const addFileExtension = () => {
    const newFileType: IFileTypeScoring = {
      fileExtension: newFileTypeName,
      singleLineCommentSyntax: '',
      multilineCommentStart: '',
      multilineCommentEnd: '',
      syntaxCharacters: '',
      scoreMultiplier: 1,
    }
    fileScores.push(newFileType)
    setFileScores([...fileScores])
    setNewFileTypeName('')
    setRequireReanalyze(true)
  }

  const save = () => {
    dispatch({
      type: 'SET_SCORES',
      scores: { generalScores: generalScores, fileScores: fileScores },
    })

    if (requireReanalyze) {
      // TODO: if requireReanalyze, redirect to home and start reanalyze
      setRequireReanalyze(false)
    }
  }

  return (
    <Modal close={togglePopup}>
      <div className={styles.container}>
        <div className={styles.header}>Edit Scoring</div>
        <Selector tabHeaders={['Change Multipliers', 'File Type Multipliers']}>
          <div className={styles.selectorContainer}>
            <Table
              excludeHeaders
              columnWidths={['6fr', '2.2fr', '1fr']}
              classes={{ data: styles.row, table: styles.table }}
              data={generalScores.map((score, i) => {
                return {
                  type: score.type,
                  input: (
                    <input
                      type="number"
                      step="0.2"
                      value={score.value}
                      className={classNames(
                        styles.generalInput,
                        styles.mediumInput
                      )}
                      onChange={e => generalScoresChange(e, i)}
                    />
                  ),
                  units: 'pts',
                }
              })}
            />
          </div>
          <div className={styles.selectorContainer}>
            <Table
              headers={[
                'File Extention',
                'Single Line Comment',
                'Multiline Comment Start',
                'Multiline Comment End',
                'Syntax Characters',
                'Weights',
                '',
              ]}
              columnWidths={['2fr', '2fr', '2fr', '2fr', '3.5fr', '1fr', '1fr']}
              classes={{ data: styles.skinnyRow, table: styles.table }}
              data={fileScores.map((score, i) => {
                return {
                  type: score.fileExtension,
                  comment: (
                    <input
                      name="singleLineCommentSyntax"
                      type="text"
                      value={score.singleLineCommentSyntax}
                      className={classNames(
                        styles.generalInput,
                        styles.mediumInput
                      )}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  commentStart: (
                    <input
                      name="multilineCommentStart"
                      type="text"
                      value={score.multilineCommentStart}
                      className={classNames(
                        styles.generalInput,
                        styles.mediumInput
                      )}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  commentEnd: (
                    <input
                      name="multilineCommentEnd"
                      type="text"
                      value={score.multilineCommentEnd}
                      className={classNames(
                        styles.generalInput,
                        styles.mediumInput
                      )}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  syntax: (
                    <input
                      name="syntaxCharacters"
                      type="text"
                      value={score.syntaxCharacters}
                      className={classNames(
                        styles.generalInput,
                        styles.longInput
                      )}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  weights: (
                    <input
                      name="scoreMultiplier"
                      type="number"
                      step="0.2"
                      value={score.scoreMultiplier}
                      className={classNames(
                        styles.generalInput,
                        styles.shortInput
                      )}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  end: (
                    <div className={styles.end}>
                      pts
                      <button
                        className={styles.delete}
                        onClick={() => deleteFileType(i)}
                      >
                        <Delete />
                      </button>
                    </div>
                  ),
                }
              })}
            />
            <div className={styles.addContainer}>
              <input
                type="text"
                placeholder="New File Extension..."
                className={styles.generalInput}
                value={newFileTypeName}
                onChange={e => setNewFileTypeName(e.target.value)}
              />
              <button
                onClick={addFileExtension}
                className={styles.addButton}
                disabled={!isValidFileType()}
              >
                Add file type
              </button>
            </div>
          </div>
        </Selector>
        <button onClick={save} className={styles.saveButton}>
          Save score settings
        </button>
        {requireReanalyze && (
          <div className={styles.warningContainer}>
            <Warning className={styles.warningIcon} /> These changes will
            require projects to be reanalyzed to be applied
          </div>
        )}
      </div>
    </Modal>
  )
}

export default UserConfigPopup
