import { useState, useRef, useEffect, DependencyList, ReactNode } from 'react'

import DefaultLoader from '../components/Loading'
import DefaultError from '../components/ErrorComp'

export interface ISuspenseProps {
  children: ReactNode
  fallback: JSX.Element | string
  error?: JSX.Element | string
}

export type TSuspenseFunction = (props: ISuspenseProps) => JSX.Element

interface ISuspenseRef {
  status: 'PENDING' | 'SUCCESS' | 'ERROR'
  Suspense?: TSuspenseFunction
  firstPass: boolean
  setData?: (data: any) => void
  setError?: (error: any) => void
}

const useSuspense = <DataType, ErrorType = Error>(
  hookFunctionHandler: (
    setData: (data: DataType) => void,
    setError: (error: ErrorType) => void
  ) => void,
  dependencies?: DependencyList
): {
  Suspense: TSuspenseFunction
  data: DataType | undefined
  error: ErrorType | undefined
} => {
  const [data, setPromiseData] = useState<DataType>()
  const [error, setPromiseError] = useState<ErrorType>()
  const [refresh, setRefresh] = useState(false)
  const { current: suspenseRef } = useRef<ISuspenseRef>({
    status: 'PENDING',
    firstPass: true,
  })

  useEffect(() => {
    if (suspenseRef.firstPass) {
      suspenseRef.firstPass = false
    } else if (suspenseRef.setData && suspenseRef.setError) {
      suspenseRef.status = 'PENDING'
      setRefresh(!refresh)
      hookFunctionHandler(suspenseRef.setData, suspenseRef.setError)
    }
  }, dependencies ?? [])

  if (suspenseRef.Suspense) {
    return {
      Suspense: suspenseRef.Suspense,
      data,
      error,
    }
  }

  suspenseRef.setData = (newData: DataType) => {
    suspenseRef.status = 'SUCCESS'
    setPromiseData(newData)
  }

  suspenseRef.setError = (newError: ErrorType) => {
    suspenseRef.status = 'ERROR'
    setPromiseError(newError)
  }

  hookFunctionHandler(suspenseRef.setData, suspenseRef.setError)

  const Suspense: TSuspenseFunction = ({
    children: LoadedComp,
    fallback: Fallback,
    error: Error,
  }) => {
    if (suspenseRef.status === 'PENDING') {
      if (typeof Fallback === 'string') {
        Fallback = <DefaultLoader message={Fallback} />
      }
      return (
        <>
          <div
            key="preventTreeUpdate"
            style={{
              display: 'none',
            }}
          >
            {LoadedComp}
          </div>
          {Fallback}
        </>
      )
    } else if (suspenseRef.status === 'ERROR' && Error) {
      if (typeof Error === 'string') {
        Error = <DefaultError message={Error} />
      }
      return Error
    } else {
      return (
        <div key="preventTreeUpdate" style={{ width: '100%', height: '100%' }}>
          {LoadedComp}
        </div>
      )
    }
  }

  suspenseRef.Suspense = Suspense

  return { Suspense, data, error }
}

export default useSuspense
