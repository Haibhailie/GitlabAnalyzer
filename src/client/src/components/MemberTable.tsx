import { useState } from 'react'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'
import { TCommitterData, TMemberData } from '../types'

import Table from '../components/Table'
import AnalyzeButton from '../components/AnalyzeButton'
import Modal from '../components/Modal'

import styles from '../css/MemberTable.module.css'

import alertIcon from '../assets/alert-octagon.svg'
import Button from './Button'

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

const testCommitterData = [
  {
    name: 'Ali Khamesy',
    email: 'akhamesy@sfu.ca',
    memberDto: {
      displayName: 'Ali Khamesy',
      id: '7',
      username: 'akhamesy',
      role: 'MAINTAINER',
      webUrl: 'http://gitlab.example.com/akhamesy',
    },
  },
  {
    name: 'Grace Luo',
    email: 'grace.r.luo@gmail.com',
    memberDto: {
      displayName: 'Grace Luo',
      id: '18',
      username: 'gracelu0',
      role: 'MAINTAINER',
      webUrl: 'http://gitlab.example.com/gracelu0',
    },
  },
  {
    name: 'Grace Luo',
    email: 'gla74@sfu.ca',
    memberDto: {
      displayName: 'Grace Luo',
      id: '5',
      username: 'gla74',
      role: 'MAINTAINER',
      webUrl: 'http://gitlab.example.com/gla74',
    },
  },
  {
    name: 'Dummy User',
    email: 'dummy@sfu.ca',
    memberDto: {
      displayName: 'Dummy User',
      id: '14',
      username: 'dummyUsername',
      role: 'MAINTAINER',
      webUrl: 'http://gitlab.example.com/dummyUsername',
    },
  },
]

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
        .then(res => {
          const status = checkResolutionStatus(testCommitterData)
          setResolutionStatus(status)
          console.log(status)
          switch (resolutionStatus) {
            case ResolutionStatus.REQUIRED:
              setModalMsg(
                'Committers have not been resolved yet! Please complete this step before proceeding.'
              )
              setShowModal(true)
              break
            case ResolutionStatus.RECOMMENDED:
              setModalMsg('Some committers may be unresolved!')
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
  // const {
  //   Suspense: CommitterSuspense,
  //   data: committerData,
  //   error: committerError,
  // } = useSuspense<TCommitterData>((setData, setError) => {
  //   jsonFetcher<TCommitterData>(`/api/project/${projectId}/committers`)
  //     .then(committers => {
  //       setData(committers)
  //       const status = checkResolutionStatus(committers)
  //       setResolutionStatus(status)
  //     })
  //     .catch(onError(setError))
  // })

  const checkResolutionStatus = (committerMap: TCommitterData) => {
    console.log(committerMap)
    console.log('ids: ' + memberIds)
    if (committerMap.length === 0) {
      return ResolutionStatus.REQUIRED
    }

    let numIgnoredCommitters = 0
    committerMap.forEach(committer => {
      if (!isProjectMember(committer.memberDto.id.toString())) {
        numIgnoredCommitters += 1
      }
    })

    console.log('num ignored: ' + numIgnoredCommitters)

    if (numIgnoredCommitters === committerMap.length) {
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

  return (
    <Suspense
      fallback="Loading Members..."
      error={error?.message ?? 'Unknown Error'}
    >
      {/* <CommitterSuspense
        fallback="Getting project committers..."
        error={committerError?.message ?? 'Unknown Error'}
      > */}
      {showModal && (
        <Modal>
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
      {/* </CommitterSuspense> */}
    </Suspense>
  )
}

export default MemberTable
