import React, { ChangeEvent, Dispatch, useContext, useState } from 'react'
import {
  IFileTypeScoring,
  IGeneralTypeScoring,
  IScores,
  UserConfigContext,
} from '../context/UserConfigContext'
import styles from '../css/UserConfigPopup.module.css'
import Selector from './Selector'

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
  const closePopup = () => {
    togglePopup()
  }

  const { userConfig, dispatch } = useContext(UserConfigContext)

  // const [fileScores, setFileScores] = useState(userConfig.fileScores)
  // const [generalScores, setGeneralScores] = useState(userConfig.generalScores)
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

    closePopup()
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>Edit Scoring</div>
      <div onClick={closePopup} className={styles.closeButton}>
        x
      </div>
      <div className={styles.scoreContainer}>
        <Selector tabHeaders={['General', 'File Type Multiplier']}>
          <div className={styles.generalContainer}>
            <table className={styles.table}>
              <colgroup>
                <col className={styles.labelColumn} />
                <col className={styles.inputColumn} />
                <col />
              </colgroup>
              <tbody>
                {generalScores.map((generalScore, index) => (
                  <tr key={index}>
                    <td>
                      <p>{generalScore.type}</p>
                    </td>
                    <td>
                      <input
                        type="number"
                        step="0.2"
                        value={generalScore.value}
                        className={styles.generalInput}
                        onChange={event => generalScoresChange(event, index)}
                      />{' '}
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
                    <tr key={index}>
                      <td>
                        <div className={styles.fileLabel}>
                          {fileScore.fileExtension}
                        </div>
                      </td>
                      <td>
                        <input
                          type="number"
                          step="0.2"
                          value={fileScore.scoreMultiplier}
                          className={styles.multiplierInput}
                          onChange={event => fileScoresChange(event, index)}
                        />{' '}
                        <div
                          className={styles.deleteButton}
                          onClick={() => deleteFileType(index)}
                        >
                          {/* TODO: REPLACE WITH ICON */}x
                        </div>
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
              <div
                onClick={addFileType}
                className={
                  newType ? styles.fileButton : styles.fileButtonDisabled
                }
              >
                Add file type
              </div>
            </div>
          </div>
        </Selector>
      </div>

      <div onClick={save} className={styles.saveButton}>
        Save score settings
      </div>
    </div>
  )
}

export default UserConfigPopup
