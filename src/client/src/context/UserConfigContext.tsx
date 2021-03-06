import { createContext, useReducer } from 'react'

export interface IFileTypeScoring {
  fileExtension: string
  scoreMultiplier: number
}

export interface IUserConfig {
  startDate?: Date
  endDate?: Date
  scoreBy: 'Merge Requests' | 'Commits'
  graphYAxis: 'Number' | 'Score'
  projectGraphBy: 'Entire Project' | 'Split By Member'
  name?: string
  locScore: number
  deleteScore: number
  commentScore: number
  spacingScore: number
  syntaxScore: number
  fileScores: Array<IFileTypeScoring>
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
  | { type: 'SET_LOC_SCORE'; locScore: number }
  | { type: 'SET_DELETE_SCORE'; deleteScore: number }
  | { type: 'SET_COMMENT_SCORE'; commentScore: number }
  | { type: 'SET_SPACING_SCORE'; spacingScore: number }
  | { type: 'SET_SYNTAX_SCORE'; syntaxScore: number }
  | { type: 'SET_FILE_SCORES'; fileScores: Array<IFileTypeScoring> }

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
    locScore: 1,
    deleteScore: 0.2,
    commentScore: 0,
    spacingScore: 0,
    syntaxScore: 0.2,
    fileScores: [],
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
    case 'SET_LOC_SCORE':
      return {
        ...state,
        locScore: action.locScore,
      }
    case 'SET_DELETE_SCORE':
      return {
        ...state,
        deleteScore: action.deleteScore,
      }
    case 'SET_COMMENT_SCORE':
      return {
        ...state,
        commentScore: action.commentScore,
      }
    case 'SET_SPACING_SCORE':
      return {
        ...state,
        spacingScore: action.spacingScore,
      }
    case 'SET_SYNTAX_SCORE':
      return {
        ...state,
        syntaxScore: action.syntaxScore,
      }
    case 'SET_FILE_SCORES':
      return {
        ...state,
        fileScores: action.fileScores,
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
  locScore: 1,
  deleteScore: 0.2,
  commentScore: 0,
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
