import { ChangeEvent, useContext, useState } from 'react'

import { UserConfigContext } from '../context/UserConfigContext'
import Selector from './Selector'
import Modal from './Modal'
import Table from './Table'

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
    fileScores[index] = {
      fileExtension: fileScores[index].fileExtension,
      scoreMultiplier: +event.target.value,
    }

    setFileScores([...fileScores])
  }

  const save = () => {
    dispatch({
      type: 'SET_SCORES',
      scores: { generalScores: generalScores, fileScores: fileScores },
    })
  }

  return (
    <Modal close={togglePopup}>
      <div className={styles.container}>
        <div className={styles.header}>Edit Scoring</div>
        <Selector tabHeaders={['Change Multipliers', 'File Type Multipliers']}>
          <div className={styles.selectorContainer}>
            <Table
              excludeHeaders
              columnWidths={['6fr', '3fr', '1fr']}
              classes={{ data: styles.row, table: styles.table }}
              data={generalScores.map((score, i) => {
                return {
                  type: score.type,
                  input: (
                    <input
                      type="number"
                      step="0.2"
                      value={score.value}
                      className={styles.generalInput}
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
              excludeHeaders
              columnWidths={['6fr', '3fr', '1fr']}
              classes={{ data: styles.skinnyRow, table: styles.table }}
              data={fileScores.map((score, i) => {
                return {
                  type: score.fileExtension,
                  input: (
                    <input
                      type="number"
                      step="0.2"
                      value={score.scoreMultiplier}
                      className={styles.generalInput}
                      onChange={e => fileScoresChange(e, i)}
                    />
                  ),
                  units: 'pts',
                }
              })}
            />
          </div>
        </Selector>
        <button onClick={save} className={styles.saveButton}>
          Save score settings
        </button>
      </div>
    </Modal>
  )
}

export default UserConfigPopup
