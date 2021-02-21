import Loading from '../components/Loading'
import ErrorComp from '../components/Error'

const onError = (setError: (error: Error) => void) => {
  return (error: Error) => {
    if (error.message === '401') {
      window.location.href = '/'
    } else if (error.message === 'Failed to fetch') {
      setError(new Error('Could not connect to server'))
    } else {
      setError(new Error('Server error. Please try again.'))
    }
  }
}

export { Loading as DefaultFallback, ErrorComp as DefaultError, onError }
