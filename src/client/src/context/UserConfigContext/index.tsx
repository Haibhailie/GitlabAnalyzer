import { noop } from 'lodash'
import { createContext, ReactNode } from 'react'
import {
  IUserConfigContext,
  TUserConfigActions,
  IUserConfigs,
  IUserConfig,
} from './types'
import reducer from './reducer'
import useAsyncReducer from '../../utils/useAsyncReducer'

export * from './types'

const defaultConfig: IUserConfig = {
  scoreBy: 'MRS',
  yAxis: 'NUMBER',
  graphMode: 'PROJECT',
  name: 'default',
  id: 'default',
  generalScores: [
    { type: 'ADD_FACTOR', value: 1 },
    { type: 'DELETE_FACTOR', value: 0.2 },
    { type: 'SYNTAX_FACTOR', value: 0.2 },
    { type: 'BLANK_FACTOR', value: 0 },
    { type: 'SPACING_FACTOR', value: 0 },
  ],
  fileScores: [
    { fileExtension: '.java', scoreMultiplier: 1 },
    { fileExtension: '.html', scoreMultiplier: 1 },
    { fileExtension: '.tsx', scoreMultiplier: 1 },
  ],
}

const initialState: IUserConfigs = {
  configs: {
    default: defaultConfig,
  },
  selected: defaultConfig,
}

export const UserConfigContext = createContext<IUserConfigContext>({
  userConfigs: initialState,
  dispatch: async (value: TUserConfigActions) => noop(value),
})

export type TUserConfigProvider = (props: {
  children: ReactNode
}) => JSX.Element

const UserConfigProvider: TUserConfigProvider = ({ children }) => {
  const [userConfigs, dispatch] = useAsyncReducer(reducer, initialState)
  return (
    <UserConfigContext.Provider value={{ userConfigs, dispatch }}>
      {children}
    </UserConfigContext.Provider>
  )
}

export default UserConfigProvider
