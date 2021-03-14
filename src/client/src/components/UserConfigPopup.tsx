import React, { ChangeEvent, Dispatch, useContext, useState } from 'react'

import {
  IFileTypeScoring,
  IGeneralTypeScoring,
  UserConfigContext,
} from '../context/UserConfigContext'
import Selector from './Selector'

import styles from '../css/UserConfigPopup.module.css'

import { ReactComponent as Delete } from '../assets/delete.svg'
import { ReactComponent as Close } from '../assets/close.svg'

interface IUserConfigPopup {
  fileScores: IFileTypeScoring[]
  setFileScores: Dispatch<React.SetStateAction<IFileTypeScoring[]>>
  generalScores: IGeneralTypeScoring[]
  setGeneralScores: Dispatch<React.SetStateAction<IGeneralTypeScoring[]>>
  togglePopup: () => void
}

const UserConfigPopup = ({
  fileScores,
  setFileScores,
  generalScores,
  setGeneralScores,
  togglePopup,
}: IUserConfigPopup) => {
  const { dispatch } = useContext(UserConfigContext)

  const [newType, setNewType] = useState('')
  const [isUniqueType, setIsUniqueType] = useState(true)
  const [currentFileScores, setCurrentFileScores] = useState(fileScores)
  const [currentGeneralScores, setCurrentGeneralScores] = useState(
    generalScores
  )

  const setNewTypeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    const input = event.target.value
    setNewType(input)
    checkUniqueType(input)
  }

  const checkUniqueType = (newType: string) => {
    const currentTypes = fileScores.map(fileScore => fileScore.fileExtension)
    if (!(currentTypes.indexOf(newType) > -1)) {
      setIsUniqueType(true)
      return true
    } else {
      setIsUniqueType(false)
      return false
    }
  }

  const addFileType = () => {
    if (newType) {
      const types = [...fileScores]
      types.push({ fileExtension: newType, scoreMultiplier: 1 })
      setFileScores([...types])
      setNewType('')
    }
  }

  const deleteFileType = (index: number) => {
    const types = [...fileScores]
    types.splice(index, 1)
    setFileScores([...types])
    checkUniqueType(newType)
  }

  const generalScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const newScores = [...generalScores]
    newScores[index] = {
      type: generalScores[index].type,
      value: +event.target.value,
    }

    setGeneralScores([...newScores])
  }

  const fileScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const newScores = [...fileScores]
    newScores[index] = {
      fileExtension: fileScores[index].fileExtension,
      scoreMultiplier: +event.target.value,
    }

    setFileScores([...newScores])
  }

  const save = () => {
    // TODO: POST CURRENT AND GET NEW SCORES
    dispatch({
      type: 'SET_SCORES',
      scores: { generalScores: generalScores, fileScores: fileScores },
    })

    setCurrentGeneralScores([...generalScores])
    setCurrentFileScores([...fileScores])

    togglePopup()
  }

  const close = () => {
    setFileScores([...currentFileScores])
    setGeneralScores([...currentGeneralScores])
    togglePopup()
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>Edit Scoring</div>
      <button onClick={close} className={styles.closeButton}>
        <Close />
      </button>
      <div className={styles.scoreContainer}>
        <Selector tabHeaders={['Change Multipliers', 'File Type Multipliers']}>
          <div className={styles.generalContainer}>
            <table className={styles.table}>
              <colgroup>
                <col className={styles.labelColumn} />
                <col className={styles.inputColumn} />
                <col />
              </colgroup>
              <tbody>
                {generalScores.map((generalScore, index) => (
                  <tr key={generalScore.type}>
                    <td className={styles.row}>{generalScore.type}</td>
                    <td className={styles.row}>
                      <input
                        type="number"
                        step="0.2"
                        value={generalScore.value}
                        className={styles.generalInput}
                        onChange={event => generalScoresChange(event, index)}
                      />
                      pts
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className={styles.generalContainer}>
            <div className={styles.fileContainer}>
              <table className={styles.table}>
                <colgroup>
                  <col className={styles.labelColumn} />
                  <col className={styles.inputColumn} />
                  <col />
                </colgroup>

                <tbody>
                  {fileScores.map((fileScore, index) => (
                    <tr key={fileScore.fileExtension}>
                      <td>
                        <div className={styles.fileLabel}>
                          {fileScore.fileExtension}
                        </div>
                      </td>
                      <td className={styles.multiplier}>
                        <input
                          type="number"
                          step="0.2"
                          value={fileScore.scoreMultiplier}
                          className={styles.multiplierInput}
                          onChange={event => fileScoresChange(event, index)}
                        />
                        <button
                          className={styles.deleteButton}
                          onClick={() => deleteFileType(index)}
                        >
                          <Delete />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className={styles.fileTypeContainer}>
              <input
                placeholder="New file extension..."
                value={newType}
                onChange={setNewTypeHandler}
                className={styles.fileExtentionInput}
              />
              {!isUniqueType && (
                <div className={styles.error}>Type already exists</div>
              )}
              <button
                onClick={addFileType}
                className={styles.fileButton}
                disabled={!newType || !isUniqueType}
              >
                Add file type
              </button>
            </div>
          </div>
        </Selector>
      </div>

      <button onClick={save} className={styles.saveButton}>
        Save score settings
      </button>
    </div>
  )
}

export default UserConfigPopup
