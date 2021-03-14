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

  const setNewTypeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    setNewType(event.target.value)
  }

  const addFileType = () => {
    if (newType) {
      const types = fileScores
      types.push({ fileExtension: newType, scoreMultiplier: 1 })
      setFileScores([...types])
      setNewType('')
    }
  }

  const deleteFileType = (index: number) => {
    fileScores.splice(index, 1)
    setFileScores([...fileScores])
  }

  const generalScoresChange = (
    event: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const newScores = generalScores
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
    const newScores = fileScores
    newScores[index] = {
      fileExtension: fileScores[index].fileExtension,
      scoreMultiplier: +event.target.value,
    }

    setFileScores([...fileScores])
  }

  const save = () => {
    // TODO: POST CURRENT AND GET NEW SCORES
    dispatch({
      type: 'SET_SCORES',
      scores: { generalScores: generalScores, fileScores: fileScores },
    })

    togglePopup()
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>Edit Scoring</div>
      <button onClick={togglePopup} className={styles.closeButton}>
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
                    <td>{generalScore.type}</td>
                    <td>
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
              <button
                onClick={addFileType}
                className={styles.fileButton}
                disabled={!newType}
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
