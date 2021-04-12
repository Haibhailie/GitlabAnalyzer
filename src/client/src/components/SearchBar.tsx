import { ChangeEvent } from 'react'

import styles from '../css/SearchBar.module.css'

export interface ISearchBarProps {
  placeholder: string
  onSearch: (event: ChangeEvent<HTMLInputElement>) => void
}

const SearchBar = ({ placeholder, onSearch }: ISearchBarProps) => {
  return (
    <input
      className={styles.input}
      placeholder={placeholder}
      onChange={onSearch}
    />
  )
}

export default SearchBar
