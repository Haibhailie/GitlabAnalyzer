export const SET_START_DATE = 'SET_START_DATE'
export const SET_END_DATE = 'SET_END_DATE'
export const SET_SCORE_BY = 'SET_SCORE_BY'
export const SET_GRAPH_Y_AXIS = 'SET_GRAPH_Y_AXIS'
export const SET_GRAPH_BY = 'SET_GRAPH_BY'
export const SET_CONFIG_NAME = 'SET_CONFIG_NAME'
export const SET_SCORES = 'SET_SCORES'
export const SET_USER_CONFIG = 'SET_USER_CONFIG'

export type TScoreBy = 'MRS' | 'COMMITS'
export type TYAxis = 'NUMBER' | 'SCORE'
export type TGraphMode = 'PROJECT' | 'MEMBER'

interface setDateAction {
  type: typeof SET_START_DATE | typeof SET_END_DATE
  date: Date
}

interface setScoreByAction {
  type: typeof SET_SCORE_BY
  scoreBy: TScoreBy
}

interface setGraphYAction {
  type: typeof SET_GRAPH_Y_AXIS
  yAxis: TYAxis
}

interface setGraphByAction {
  type: typeof SET_GRAPH_BY
  graphMode: TGraphMode
}

interface setConfigNameAction {
  type: typeof SET_CONFIG_NAME
  name: string
}

interface setScores {
  type: typeof SET_SCORES
  scores: IScores
}

interface setUserConfig {
  type: typeof SET_USER_CONFIG
  userConfig: IUserConfig
}

export type TUserConfigActions =
  | setDateAction
  | setScoreByAction
  | setGraphByAction
  | setGraphYAction
  | setConfigNameAction
  | setScores
  | setUserConfig

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
  scoreBy: TScoreBy
  yAxis: TYAxis
  graphMode: TGraphMode
  name: string
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export interface IScores {
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export type TUserConfigReducer = (
  state: IUserConfig,
  action: TUserConfigActions
) => IUserConfig

export interface IUserConfigContext {
  userConfig: IUserConfig
  dispatch: React.Dispatch<TUserConfigActions>
}
