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
  let status = 'PENDING'

  const promise = new Promise((resolve, reject) => {
    const setData = (newData: DataType) => {
      data = newData
      status = 'SUCCESS'
      resolve(true)
    }

    const setError = (newError: ErrorType) => {
      error = newError
      status = 'ERROR'
      reject(true)
    }

    hookFunctionHandler(setData, setError)
  })

  const updateState = (
    success: (data: DataType) => void,
    failed: (error: ErrorType) => void
  ) => {
    if (status === 'SUCCESS') {
      success(data)
    } else if (status === 'ERROR') {
      failed(error)
    } else {
      promise.then(() => success(data)).catch(() => failed(error))
    }
  }

  const Suspense = ({
    children: LoadedComp,
    fallback: Fallback,
    error: Error,
  }: ISuspenseProps) => {
    const [loading, setLoading] = useState(true)
    const [failed, setFailed] = useState(false)

    useEffect(() => {
      updateState(
        () => setLoading(false),
        () => {
          setLoading(false)
          setFailed(true)
        }
      )
    }, [])

    if (loading) {
      return Fallback
    } else if (failed && Error) {
      return Error
    } else {
      return LoadedComp
    }
  }

  const useContent = () => {
    const [state, setState] = useState({
      data,
      error,
    })

    useEffect(() => {
      updateState(
        data => {
          setState({
            ...state,
            data,
          })
        },
        error => {
          setState({
            ...state,
            error,
          })
        }
      )
    }, [])

    return { data, error }
  }

  return [Suspense, useContent]
}

export default withSuspense
