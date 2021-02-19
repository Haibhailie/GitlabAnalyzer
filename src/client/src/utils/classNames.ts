type classes = Array<string | number | undefined | null>

const classNames = (...classes: classes) => {
  return classes.filter(cls => cls !== undefined && cls !== null).join(' ')
}

export default classNames
