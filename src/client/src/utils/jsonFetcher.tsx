const base = 'http://localhost:8080'

const jsonFetcher = <DataType extends unknown>(
  url: string,
  options?: RequestInit
) => {
  options = {
    credentials: 'include',
    ...options,
  }

  return new Promise<DataType>((resolve, reject) => {
    fetch(`${base}${url}`, options)
      .then(res => {
        if (res.status === 200) {
          res.json().then(resolve)
        } else {
          reject(new Error(res.status.toString()))
        }
      })
      .catch(error => reject(error))
  })
}

export default jsonFetcher
