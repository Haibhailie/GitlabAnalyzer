import { round } from 'lodash'

const kB = 1024
const MB = kB * 1024
const GB = MB * 1024
const TB = GB * 1024

const bytesConverter = (numBytes: number) => {
  if (numBytes < kB) {
    return `${numBytes} bytes`
  } else if (numBytes < MB) {
    return `${round(numBytes / kB, 2)} kB`
  } else if (numBytes < GB) {
    return `${round(numBytes / MB, 2)} MB`
  } else if (numBytes < TB) {
    return `${round(numBytes / GB, 2)} GB`
  } else {
    return `${round(numBytes / TB, 2)} TB`
  }
}

export default bytesConverter
