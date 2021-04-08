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
        id: '5',
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
        id: '6',
        username: 'gla74',
        role: 'MAINTAINER',
        webUrl: 'http://gitlab.example.com/gracelu0',
      },
    },
    {
      name: 'Dummy User',
      email: 'dummy@sfu.ca',
      memberDto: {
        displayName: 'Dummy User',
        id: '8',
        username: 'dummyUsername',
        role: 'MAINTAINER',
        webUrl: 'http://gitlab.example.com/dummyUsername',
      },
    },
  ]

  const createCommitterMap = (committers: TCommitterData) => {
    const memberCommitterMap: TMemberCommitterMap = []
    committers.forEach(committer => {
      const map: IMemberCommitterMap = {
        email: committer.email,
        memberId: committer.memberDto.id
          ? parseInt(committer.memberDto.id)
          : '',
      }
      console.log(map)
      memberCommitterMap.push(map)
    })
    console.log(memberCommitterMap)
    setFormData(memberCommitterMap)
  }

  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedCommitter = event.target.name
    const selectedMemberId = event.target.value

    console.log(event.target.name, event.target.value)

    setFormData(
      formData.map(committer =>
        committer.email === selectedCommitter
          ? { ...committer, memberId: selectedMemberId }
          : committer
      )
    )
  }

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    console.log(formData)

    jsonFetcher(`/api/project/${id}/committers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData),
      responseIsEmpty: true,
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
              break
          }
        }
      })
      .catch(() => {
        setErrorMsg('Could not connect to server')
      })
  }

  const {
    Suspense: MemberSuspense,
    data: memberData,
    error: memberError,
  } = useSuspense<TMemberData>((setData, setError) => {
    jsonFetcher<TMemberData>(`/api/project/${id}/members`)
      .then(setData)
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
        createCommitterMap(testCommitterData)
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
                testCommitterData?.map(({ name, email, memberDto }) => {
                  return {
                    name,
                    email,
                    memberDto: memberData ? (
                      <MemberSelect
                        data={memberData}
                        defaultSelected={memberDto?.id ?? ''}
                        committerEmail={email}
                        onChange={handleChange}
                      />
                    ) : null,
                  }
                }) ?? [{}]
              }
            />
            <button type="submit" className={styles.button}>
              Save
            </button>
          </form>
        </div>
      </CommitterSuspense>
    </MemberSuspense>
  )
}

export default MemberResolution
