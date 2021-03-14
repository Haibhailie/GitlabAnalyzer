import { createContext, useReducer } from 'react'

export interface IFileTypeScoring {
  fileExtension: string
  scoreMultiplier: number
}

export interface IGeneralTypeScoring {
  type: string
  value: number
}

export interface IUserConfig {
  startDate?: Date
  endDate?: Date
  scoreBy: 'Merge Requests' | 'Commits'
  graphYAxis: 'Number' | 'Score'
  projectGraphBy: 'Entire Project' | 'Split By Member'
  name: string
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export interface IScores {
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export type IUserConfigReducerAction =
  | { type: 'SET_START_DATE'; startDate: Date }
  | { type: 'SET_END_DATE'; endDate: Date }
  | { type: 'SET_SCORE_BY'; scoreBy: 'Merge Requests' | 'Commits' }
  | { type: 'SET_GRAPH_Y_AXIS'; graphYAxis: 'Number' | 'Score' }
  | {
      type: 'SET_PROJECT_GRAPH_BY'
      projectGraphBy: 'Entire Project' | 'Split By Member'
    }
  | { type: 'SET_NAME'; name: string }
  | { type: 'SET_SCORES'; scores: IScores }
  | { type: 'SET_USER_CONFIG'; userConfig: IUserConfig }

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
    graphYAxis: 'Number',
    projectGraphBy: 'Entire Project',
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
  },
  dispatch: (value: IUserConfigReducerAction) => console.log(value),
})

const reducer: IUserConfigReducer = (
  state,
  action: IUserConfigReducerAction
) => {
  switch (action.type) {
    case 'SET_START_DATE':
      return {
        ...state,
        startDate: action.startDate,
      }
    case 'SET_END_DATE':
      return {
        ...state,
        endDate: action.endDate,
      }
    case 'SET_SCORE_BY':
      return {
        ...state,
        scoreBy: action.scoreBy,
      }
    case 'SET_GRAPH_Y_AXIS':
      return {
        ...state,
        graphYAxis: action.graphYAxis,
      }
    case 'SET_PROJECT_GRAPH_BY':
      return {
        ...state,
        projectGraphBy: action.projectGraphBy,
      }
    case 'SET_NAME':
      return {
        ...state,
        name: action.name,
      }
    case 'SET_SCORES':
      return {
        ...state,
        generalScores: action.scores.generalScores,
        fileScores: action.scores.fileScores,
      }
    case 'SET_USER_CONFIG':
      return {
        ...action.userConfig,
      }
    default:
      return state
  }
}
const initialState: IUserConfig = {
  scoreBy: 'Merge Requests',
  graphYAxis: 'Number',
  projectGraphBy: 'Entire Project',
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
