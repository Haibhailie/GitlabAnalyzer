type classes = Array<string | number | boolean | undefined | null>

const classNames = (...classes: classes) => {
  return classes.filter(cls => cls).join(' ')
}

export default classNames
