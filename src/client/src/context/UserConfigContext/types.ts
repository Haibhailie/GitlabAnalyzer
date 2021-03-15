export const SET_START_DATE = 'SET_START_DATE'
export const SET_END_DATE = 'SET_END_DATE'
export const SET_SCORE_BY = 'SET_SCORE_BY'
export const SET_GRAPH_Y_AXIS = 'SET_GRAPH_Y_AXIS'
export const SET_GRAPH_BY = 'SET_GRAPH_BY'
export const SET_CONFIG_NAME = 'SET_CONFIG_NAME'
export const SET_SCORES = 'SET_SCORES'
export const SET_CONFIG = 'SET_CONFIG'
export const UPDATE_CONFIG = 'UPDATE_CONFIG'
export const ADD_CONFIG = 'ADD_CONFIG'
export const DELETE_CONFIG = 'DELETE_CONFIG'
export const FLUSH_CONFIGS = 'FLUSH_CONFIGS'

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
  type: typeof SET_CONFIG
  id: string
}

interface updateUserConfig {
  type: typeof UPDATE_CONFIG
  id: string
}

interface addUserConfig {
  type: typeof ADD_CONFIG
  name: string
}

interface deleteUserConfig {
  type: typeof DELETE_CONFIG
  id: string
}

interface flushUserConfigs {
  type: typeof FLUSH_CONFIGS
}

export type TUserConfigActions =
  | setDateAction
  | setScoreByAction
  | setGraphByAction
  | setGraphYAction
  | setConfigNameAction
  | setScores
  | setUserConfig
  | updateUserConfig
  | addUserConfig
  | deleteUserConfig
  | flushUserConfigs

export interface IFileTypeScoring {
  fileExtension: string
  scoreMultiplier: number
}

export interface IGeneralTypeScoring {
  type: string
  value: number
}

export interface IScores {
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export interface IUserConfig {
  startDate?: Date
  endDate?: Date
  id?: string
  scoreBy: TScoreBy
  yAxis: TYAxis
  graphMode: TGraphMode
  name: string
  generalScores: IGeneralTypeScoring[]
  fileScores: IFileTypeScoring[]
}

export interface IUserConfigs {
  configs: Record<string, IUserConfig>
  selected: IUserConfig
}

export type TUserConfigReducer = (
  state: IUserConfigs,
  action: TUserConfigActions
) => Promise<IUserConfigs>

export interface IUserConfigContext {
  userConfigs: IUserConfigs
  dispatch: React.Dispatch<TUserConfigActions>
}
