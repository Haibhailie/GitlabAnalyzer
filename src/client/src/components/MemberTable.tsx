import { useState } from 'react'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'
import { TCommitterData, TMemberData } from '../types'

import Table from '../components/Table'
import AnalyzeButton from '../components/AnalyzeButton'
import Modal from '../components/Modal'
import Button from '../components/Button'

import styles from '../css/MemberTable.module.css'

import alertIcon from '../assets/alert-octagon.svg'

export interface IMemberTableProps {
  projectId: string
  projectName?: string
}

enum ResolutionStatus {
  REQUIRED,
  RECOMMENDED,
  COMPLETED,
  ALL_IGNORED,
}

const MemberTable = ({ projectId, projectName }: IMemberTableProps) => {
  const history = useHistory()
  const [resolutionStatus, setResolutionStatus] = useState<ResolutionStatus>()
  const [memberIds, setMemberIds] = useState<string[]>([])
  const [showModal, setShowModal] = useState(false)
  const [modalMsg, setModalMsg] = useState('')
  const { Suspense, data, error } = useSuspense<TMemberData>(
    (setData, setError) => {
      jsonFetcher<TMemberData>(`/api/project/${projectId}/members`)
        .then(members => {
          setData(members)
          const ids = members.map(member => member.id.toString())
          setMemberIds(ids)
        })
        .catch(onError(setError))
    }
  )
  const {
    Suspense: CommitterSuspense,
    data: committerData,
    error: committerError,
  } = useSuspense<TCommitterData>(
    (setData, setError) => {
      jsonFetcher<TCommitterData>(`/api/project/${projectId}/committers`)
        .then(committers => {
          setData(committers)
          const status = checkResolutionStatus()
          setResolutionStatus(status)
          switch (resolutionStatus) {
            case ResolutionStatus.REQUIRED:
              setModalMsg(
                'Committers have not been resolved yet! Please complete this step before proceeding.'
              )
              setShowModal(true)
              break
            case ResolutionStatus.RECOMMENDED:
              setModalMsg('Warning: Some committers may be unresolved!')
              setShowModal(true)
              break
            case ResolutionStatus.ALL_IGNORED:
              setModalMsg(
                'All committers are currently ignored. Please resolve committers before proceeding. '
              )
              setShowModal(true)
              break
            case ResolutionStatus.COMPLETED:
              setShowModal(false)
              break
            default:
              break
          }
        })
        .catch(onError(setError))
    },
    [resolutionStatus]
  )

  const checkResolutionStatus = () => {
    if (committerData?.length === 0) {
      return ResolutionStatus.REQUIRED
    }

    let numIgnoredCommitters = 0
    committerData?.forEach(committer => {
      if (!isProjectMember(committer.member.id.toString())) {
        numIgnoredCommitters += 1
      }
    })

    if (numIgnoredCommitters === committerData?.length) {
      return ResolutionStatus.ALL_IGNORED
    } else if (numIgnoredCommitters > 0) {
      return ResolutionStatus.RECOMMENDED
    } else if (numIgnoredCommitters === 0) {
      return ResolutionStatus.COMPLETED
    }
  }

  const isProjectMember = (id: string) => {
    return memberIds.includes(id)
  }

  const onAnalyze = (id: string) => {
    history.push(`/project/${projectId}/member/${id}`)
  }

  const isResolutionOptional = () => {
    return (
      resolutionStatus !== ResolutionStatus.REQUIRED &&
      resolutionStatus !== ResolutionStatus.ALL_IGNORED
    )
  }

  const toggleModal = () => {
    setShowModal(!showModal)
  }

  return (
    <Suspense
      fallback="Loading Members..."
      error={error?.message ?? 'Unknown Error'}
    >
      <CommitterSuspense
        fallback="Getting project committers..."
        error={committerError?.message ?? 'Unknown Error'}
      >
        {showModal && (
          <Modal close={isResolutionOptional() ? toggleModal : undefined}>
            <img src={alertIcon} className={styles.icon} />
            <div className={styles.modalMsgContainer}>{modalMsg}</div>
            <Button
              message="Resolve committers"
              destPath={`/project/${projectId}/memberResolution`}
            />
          </Modal>
        )}

        <Table
          sortable
          headers={['Member', 'Username', 'Role', '']}
          columnWidths={['2fr', '2fr', '2fr', '3fr']}
          classes={{
            container: styles.tableContainer,
            table: styles.table,
            header: styles.theader,
            data: styles.tdata,
          }}
          title={`Members of ${projectName}`}
          data={
            data?.map(({ displayName, id, role, username }) => {
              return {
                displayName,
                username,
                role,
                analyze: (
                  <AnalyzeButton
                    message={`Analyze ${`${displayName} `.split(' ')[0]}`}
                    id={id}
                    onClick={onAnalyze}
                    className={styles.analyze}
                  />
                ),
              }
            }) ?? [{}]
          }
        />
        <Button
          message="Member to Committer Resolution"
          destPath={`/project/${projectId}/memberResolution`}
        />
      </CommitterSuspense>
    </Suspense>
  )
}

export default MemberTable
