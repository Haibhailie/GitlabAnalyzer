import Loading from '../components/Loading'
import ErrorComp from '../components/Error'
import { NETWORK_ERROR, SERVER_ERROR, UNKNOWN_ERROR } from './constants'

const onError = (setError: (error: Error) => void) => {
  return (error: Error) => {
    if (error.message === '401') {
      window.location.href = '/'
    } else if (error.message === 'Failed to fetch') {
      setError(new Error(NETWORK_ERROR))
    } else if (error.message === '500') {
      setError(new Error(SERVER_ERROR))
    } else {
      setError(new Error(UNKNOWN_ERROR))
    }
  }
}

export { Loading as DefaultFallback, ErrorComp as DefaultError, onError }
