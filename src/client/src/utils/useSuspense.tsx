import { useState, useRef } from 'react'

export interface ISuspenseProps {
  children: JSX.Element
  fallback: JSX.Element
  error?: JSX.Element
}

export type TSuspenseFunction = (props: ISuspenseProps) => JSX.Element

const useSuspense = <DataType, ErrorType>(
  hookFunctionHandler: (
    setData: (data: DataType) => void,
    setError: (error: ErrorType) => void
  ) => void
): {
  Suspense: TSuspenseFunction
  data: DataType | undefined
  error: ErrorType | undefined
} => {
  const [data, setPromiseData] = useState<DataType>()
  const [error, setPromiseError] = useState<ErrorType>()
  const suspenseRef = useRef<TSuspenseFunction | null>(null)
  const status = useRef('PENDING')

  if (suspenseRef.current !== null) {
    return {
      Suspense: suspenseRef.current,
      data,
      error,
    }
  }

  const setData = (newData: DataType) => {
    status.current = 'SUCCESS'
    setPromiseData(newData)
  }

  const setError = (newError: ErrorType) => {
    status.current = 'ERROR'
    setPromiseError(newError)
  }

  hookFunctionHandler(setData, setError)

  const Suspense: TSuspenseFunction = ({
    children: LoadedComp,
    fallback: Fallback,
    error: Error,
  }) => {
    if (status.current === 'PENDING') {
      return Fallback
    } else if (status.current === 'ERROR' && Error) {
      return Error
    } else {
      return LoadedComp
    }
  }

  suspenseRef.current = Suspense

  return { Suspense, data, error }
}

export default useSuspense
