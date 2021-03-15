import { ReactNode } from 'react'

import styles from '../css/Modal.module.css'

import { ReactComponent as CloseIcon } from '../assets/close.svg'

export interface IModalProps {
  children: ReactNode
  close?: () => void
}

const Modal = ({ children, close }: IModalProps) => {
  return (
    <div className={styles.container}>
      <div className={styles.modal}>
        {children}
        {close && (
          <button onClick={close} className={styles.close}>
            <CloseIcon />
          </button>
        )}
      </div>
    </div>
  )
}

export default Modal
