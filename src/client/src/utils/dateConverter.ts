const currYear = new Date().getFullYear()

const dateConverter = (epoch: number, includeTime?: boolean) => {
  const date = new Date(epoch)

  const options = {
    month: 'short',
    day: 'numeric',
    year: date.getFullYear() != currYear ? 'numeric' : undefined,
    hour: includeTime ? 'numeric' : undefined,
    minute: includeTime ? 'numeric' : undefined,
  }

  return date.toLocaleDateString('en-US', options)
}

export default dateConverter
