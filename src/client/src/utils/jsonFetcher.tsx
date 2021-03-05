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
    ...options,
  }

  return new Promise<DataType>((resolve, reject) => {
    fetch(`${URLBASE}${url}`, options)
      .then(res => {
        if (res.status === 200) {
          if (options?.responseIsEmpty) {
            resolve(res.status as DataType)
          } else {
            res.json().then(resolve)
          }
        } else {
          reject(new Error(res.status.toString()))
        }
      })
      .catch(error => reject(error))
  })
}

export default jsonFetcher
