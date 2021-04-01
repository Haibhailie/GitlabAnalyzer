import styles from '../css/ExternalLink.module.css'

import linkIcon from '../assets/link.svg'

export interface IExternalLinkProps {
  link: string
}

const ExternalLink = ({ link }: IExternalLinkProps) => {
  return (
    <a href={link} target="_blank" rel="noopener noreferrer">
      <img src={linkIcon} />
    </a>
  )
}

export default ExternalLink
