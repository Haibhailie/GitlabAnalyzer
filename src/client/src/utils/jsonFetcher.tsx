const jsonFetcher = (url: string, options?: RequestInit) => {
  options = {
    credentials: 'include',
    ...options,
  }

  return new Promise((resolve, reject) => {
    fetch(url, options).then(res => {
      if (res.status === 200) {
        res.json().then(resolve)
      } else {
        reject(res.status)
      }
    })
  })
}

export default jsonFetcher
