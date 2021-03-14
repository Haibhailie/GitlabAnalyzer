import { noop } from 'lodash'
import { createContext, useReducer, ReactNode } from 'react'
import { IUserConfigContext, TUserConfigActions, IUserConfig } from './types'
import reducer from './reducer'

export * from './types'

const initialState: IUserConfig = {
  scoreBy: 'MRS',
  yAxis: 'NUMBER',
  graphMode: 'PROJECT',
  name: '',
  generalScores: [
    { type: 'New line of code', value: 1 },
    { type: 'Deleting a line', value: 0.2 },
    { type: 'Comment/blank', value: 0 },
    { type: 'Spacing change', value: 0 },
    { type: 'Syntax only', value: 0.2 },
  ],
  fileScores: [
    { fileExtension: '.java', scoreMultiplier: 1 },
    { fileExtension: '.html', scoreMultiplier: 1 },
    { fileExtension: '.tsx', scoreMultiplier: 1 },
  ],
}

export const UserConfigContext = createContext<IUserConfigContext>({
  userConfig: initialState,
  dispatch: (value: TUserConfigActions) => noop(value),
})

export type TUserConfigProvider = (props: {
  children: ReactNode
}) => JSX.Element

const UserConfigProvider: TUserConfigProvider = ({ children }) => {
  const [userConfig, dispatch] = useReducer(reducer, initialState)
  return (
    <UserConfigContext.Provider value={{ userConfig, dispatch }}>
      {children}
    </UserConfigContext.Provider>
  )
}

export default UserConfigProvider
