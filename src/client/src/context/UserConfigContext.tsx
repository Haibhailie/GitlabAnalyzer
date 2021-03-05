import { createContext, useReducer } from 'react'

export interface IFileTypeScoring {
  fileExtension: string
  scoreMultiplier: number
}

export interface IUserConfig {
  startDate?: Date
  endDate?: Date
  scoreBy: 'Merge Requests' | 'Commits'
  graphyYAxis: 'Number' | 'Score'
  projectGraphBy: 'Entire Project' | 'Split By Member'
  name?: string
  locScore: number
  deleteScore: number
  fluffScore: number
  spacingScore: number
  syntaxScore: number
  fileScores: Array<IFileTypeScoring>
}

export type IUserConfigReducerAction = {
  type: 'SET_CONFIG'
  userConfig: IUserConfig
}

export type IUserConfigReducer = (
  state: IUserConfig,
  action: IUserConfigReducerAction
) => IUserConfig

export interface IUserConfigContext {
  userConfig: IUserConfig
  dispatch: React.Dispatch<IUserConfigReducerAction>
}

export const UserConfigContext = createContext<IUserConfigContext>({
  userConfig: {
    scoreBy: 'Merge Requests',
    graphyYAxis: 'Number',
    projectGraphBy: 'Entire Project',
    locScore: 1,
    deleteScore: 0.2,
    fluffScore: 0,
    spacingScore: 0,
    syntaxScore: 0.2,
    fileScores: [],
  },
  dispatch: (value: IUserConfigReducerAction) => console.log(value),
})

const reducer: IUserConfigReducer = (state, { type, userConfig }) => {
  switch (type) {
    case 'SET_CONFIG':
      return {
        ...userConfig,
      }
    default:
      return state
  }
}
const initialState: IUserConfig = {
  scoreBy: 'Merge Requests',
  graphyYAxis: 'Number',
  projectGraphBy: 'Entire Project',
  locScore: 1,
  deleteScore: 0.2,
  fluffScore: 0,
  spacingScore: 0,
  syntaxScore: 0.2,
  fileScores: [],
}

const UserConfigProvider = ({
  children,
}: {
  children: JSX.Element[] | JSX.Element
}) => {
  const [userConfig, dispatch] = useReducer(reducer, initialState)
  return (
    <UserConfigContext.Provider value={{ userConfig, dispatch }}>
      {children}
    </UserConfigContext.Provider>
  )
}

export default UserConfigProvider
