import React, { useState } from 'react'
import { useParams, useHistory } from 'react-router-dom'
import jsonFetcher from '../utils/jsonFetcher'
import useSuspense from '../utils/useSuspense'
import { onError } from '../utils/suspenseDefaults'
import {
  TCommitterData,
  TMemberData,
  TMemberCommitterMap,
  IMemberCommitterMap,
} from '../types'

import Table from '../components/Table'
import MemberSelect from '../components/MemberSelect'

import styles from '../css/MemberResolution.module.css'

const MemberResolution = () => {
  const { id } = useParams<{ id: string }>()
  const history = useHistory()
  const [formData, setFormData] = useState<TMemberCommitterMap>([])
  const [errorMsg, setErrorMsg] = useState('')
  const [memberIds, setMemberIds] = useState<string[]>([])

  const createCommitterMap = (committers: TCommitterData) => {
    const memberCommitterMap: TMemberCommitterMap = []

    committers.forEach(committer => {
      const map: IMemberCommitterMap = {
        email: committer.email,
        memberId: committer.member.id ? parseInt(committer.member.id) : '',
      }
      memberCommitterMap.push(map)
    })
    return memberCommitterMap
  }

  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedCommitter = event.target.name
    const selectedMemberId =
      event.target.value === ''
        ? event.target.value
        : parseInt(event.target.value)

    const updatedCommitter = {
      email: selectedCommitter,
      memberId: selectedMemberId,
    }

    const updatedMap = [...formData]
    const index = updatedMap.findIndex(c => c.email === selectedCommitter)
    if (index === -1) {
      updatedMap.push(updatedCommitter)
    } else {
      updatedMap[index] = updatedCommitter
    }

    setFormData(updatedMap)
  }

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const submittedMap = [...formData]

    const committerMap: TMemberCommitterMap = []

    submittedMap.forEach(committer => {
      if (
        committer.memberId !== '' &&
        isProjectMember(committer.memberId.toString())
      ) {
        committerMap.push(committer)
      }
    })

    jsonFetcher(`/api/project/${id}/committers`, {
      method: 'POST',
      body: JSON.stringify(committerMap),
      headers: { 'Content-Type': 'application/json' },
      responseIsEmpty: true,
      credentials: 'include',
    })
      .then(res => {
        if (res === 200) {
          alert('Member to committer mapping saved!')
          history.push(`/project/${id}`)
        } else {
          switch (res) {
            case 400:
              setErrorMsg('Please select an option for each committer')
              break
            case 401:
              setErrorMsg('Invalid Personal Access Token')
              break
            case 404:
              setErrorMsg('Project not found')
              break
            case 500:
              setErrorMsg('An unknown error occurred')
              break
            default:
              setErrorMsg('An unknown error occurred')
              break
          }
        }
      })
      .catch(() => {
        setErrorMsg('Could not connect to server')
      })
  }

  const isProjectMember = (id: string) => {
    return memberIds.includes(id)
  }

  const {
    Suspense: MemberSuspense,
    data: memberData,
    error: memberError,
  } = useSuspense<TMemberData>((setData, setError) => {
    jsonFetcher<TMemberData>(`/api/project/${id}/members`)
      .then(members => {
        setData(members)
        const ids = members.map(member => member.id.toString())
        setMemberIds(ids)
      })
      .catch(onError(setError))
  })

  const {
    Suspense: CommitterSuspense,
    data: committerData,
    error: committerError,
  } = useSuspense<TCommitterData>((setData, setError) => {
    jsonFetcher<TCommitterData>(`/api/project/${id}/committers`)
      .then(committers => {
        setData(committers)
        const memberCommitterMap = createCommitterMap(committers)
        setFormData(memberCommitterMap)
      })
      .catch(onError(setError))
  })

  return (
    <MemberSuspense
      fallback="Getting project members..."
      error={memberError?.message ?? 'Unknown Error'}
    >
      <CommitterSuspense
        fallback="Getting project committers..."
        error={committerError?.message ?? 'Unknown Error'}
      >
        <div className={styles.container}>
          <h1 className={styles.header}>Member to Committer Resolution</h1>
          <form className={styles.formContainer} onSubmit={handleSubmit}>
            {errorMsg && <h3 className={styles.errorAlert}>{errorMsg}</h3>}
            <Table
              headers={['Committer', 'Email', 'Member']}
              columnWidths={['2fr', '2fr', '3fr']}
              classes={{
                container: styles.tableContainer,
                table: styles.table,
                header: styles.theader,
                data: styles.tdata,
              }}
              data={
                committerData?.map(({ name, email, member }) => {
                  return {
                    name,
                    email,
                    member: memberData ? (
                      <MemberSelect
                        data={memberData}
                        defaultSelected={member.id}
                        committerEmail={email}
                        onChange={handleChange}
                      />
                    ) : null,
                  }
                }) ?? [{}]
              }
            />
            <input type="submit" className={styles.button} value="Save" />
          </form>
        </div>
      </CommitterSuspense>
    </MemberSuspense>
  )
}

export default MemberResolution
