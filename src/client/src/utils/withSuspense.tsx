import { useEffect, useState } from 'react'

export interface ISuspenseProps {
  children: JSX.Element
  fallback: JSX.Element
  error?: JSX.Element
}

const withSuspense = <DataType, ErrorType>(
  hookFunctionHandler: (
    setData: (data: DataType) => void,
    setError: (error: ErrorType) => void
  ) => void
): [
  (props: ISuspenseProps) => JSX.Element,
  () => { data: DataType; error: ErrorType }
] => {
  let data: DataType
  let error: ErrorType

  const status = new Promise((resolve, reject) => {
    const setData = (newData: DataType) => {
      data = newData
      resolve(true)
    }

    const setError = (newError: ErrorType) => {
      error = newError
      reject(true)
    }

    hookFunctionHandler(setData, setError)
  })

  const Suspense = ({
    children: LoadedComp,
    fallback: Fallback,
    error: Error,
  }: ISuspenseProps) => {
    const [loading, setLoading] = useState(true)
    const [failed, setFailed] = useState(false)

    useEffect(() => {
      status.catch(() => setFailed(true)).finally(() => setLoading(false))
    }, [])

    if (loading) {
      return Fallback
    } else if (failed && Error) {
      return Error
    } else {
      return LoadedComp
    }
  }

  const getContent = () => {
    return { data, error }
  }

  return [Suspense, getContent]
}

export default withSuspense
