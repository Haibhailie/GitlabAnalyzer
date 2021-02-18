import { useEffect, useState } from 'react'

export interface ISuspenseProps {
  children: JSX.Element
  fallback: JSX.Element
  error?: JSX.Element
}

const useSuspense = <DataType, ErrorType>(
  hookFunctionHandler: (
    setData: (data: DataType) => void,
    setError: (error: ErrorType) => void
  ) => void
): {
  Suspense: (props: ISuspenseProps) => JSX.Element
  data: DataType | undefined
  error: ErrorType | undefined
} => {
  const [status, setStatus] = useState('PENDING')
  const [data, setPromiseData] = useState<DataType>()
  const [error, setPromiseError] = useState<ErrorType>()

  useEffect(() => {
    const setData = (newData: DataType) => {
      setPromiseData(newData)
      setStatus('SUCCESS')
    }

    const setError = (newError: ErrorType) => {
      setPromiseError(newError)
      setStatus('ERROR')
    }

    hookFunctionHandler(setData, setError)
  }, [])

  const Suspense = ({
    children: LoadedComp,
    fallback: Fallback,
    error: Error,
  }: ISuspenseProps) => {
    if (status === 'PENDING') {
      return Fallback
    } else if (status === 'ERROR' && Error) {
      return Error
    } else {
      return LoadedComp
    }
  }

  return { Suspense, data, error }
}

export default useSuspense
