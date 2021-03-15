import { URLBASE } from './constants'

export interface jsonFetcherOptions extends RequestInit {
  responseIsEmpty?: boolean
}

const jsonFetcher = <DataType extends unknown>(
  url: string,
  options?: jsonFetcherOptions
) => {
  options = {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    ...options,
  }

  return new Promise<DataType>((resolve, reject) => {
    fetch(`${URLBASE}${url}`, options)
      .then(res => {
        if (options?.responseIsEmpty) {
          resolve(res.status as DataType)
        } else if (res.status === 200) {
          res.json().then(resolve)
        } else {
          reject(new Error(res.status.toString()))
        }
      })
      .catch(error => reject(error))
  })
}

export default jsonFetcher
