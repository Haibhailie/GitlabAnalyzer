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
    { type: 'New line of code', value: 1 },
    { type: 'Deleting a line', value: 0.2 },
    { type: 'Comment/blank', value: 0 },
    { type: 'Spacing change', value: 0 },
    { type: 'Syntax only', value: 0.2 },
  ],
  fileScores: [
    {
      fileExtension: '.java',
      singleLineCommentSyntax: '',
      multilineCommentStart: '',
      multilineCommentEnd: '',
      syntaxCharacters: '',
      scoreMultiplier: 1,
    },
    {
      fileExtension: '.html',
      singleLineCommentSyntax: '',
      multilineCommentStart: '',
      multilineCommentEnd: '',
      syntaxCharacters: '',
      scoreMultiplier: 1,
    },
    {
      fileExtension: '.tsx',
      singleLineCommentSyntax: '',
      multilineCommentStart: '',
      multilineCommentEnd: '',
      syntaxCharacters: '',
      scoreMultiplier: 1,
    },
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
