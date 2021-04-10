import { useState } from 'react'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import { useHistory } from 'react-router-dom'
import { TCommitterData, TMemberData } from '../types'

import Table from '../components/Table'
import AnalyzeButton from '../components/AnalyzeButton'

import styles from '../css/MemberTable.module.css'
import Modal from './Modal'

export interface IActivityGraphProps {
  projectId: string
  projectName?: string
}

enum ResolutionStatus {
  REQUIRED,
  RECOMMENDED,
  COMPLETED,
}

const MemberTable = ({ projectId, projectName }: IActivityGraphProps) => {
  const history = useHistory()
  const [resolutionStatus, setResolutionStatus] = useState<ResolutionStatus>()
  const [memberIds, setMemberIds] = useState<string[]>([])
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
  } = useSuspense<TCommitterData>((setData, setError) => {
    jsonFetcher<TCommitterData>(`/api/project/${projectId}/committers`)
      .then(committers => {
        setData(committers)
        const status = checkResolutionStatus(committers)
        setResolutionStatus(status)
      })
      .catch(onError(setError))
  })

  const checkResolutionStatus = (committerMap: TCommitterData) => {
    if (committerMap === []) {
      return ResolutionStatus.REQUIRED
    }

    let numIgnoredCommitters = 0
    committerMap.forEach(committer => {
      if (!isProjectMember(committer.memberDto.id)) {
        numIgnoredCommitters += 1
      }
    })
    if (numIgnoredCommitters > 0) {
      return ResolutionStatus.RECOMMENDED
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
      <CommitterSuspense
        fallback="Getting project committers..."
        error={committerError?.message ?? 'Unknown Error'}
      >
        {resolutionStatus === ResolutionStatus.REQUIRED && (
          <Modal>
            <div>Member to committer resolution required</div>
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
      </CommitterSuspense>
    </Suspense>
  )
}

export default MemberTable
