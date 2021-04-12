import { ICommitData, IFileData, IMemberData, IMergeData } from '../../types'
import { IUserConfig } from '../../context/UserConfigContext'

export const IGNORE_MR = 'IGNORE_MR'
export const IGNORE_MR_FILE = 'IGNORE_MR_FILE'
export const IGNORE_COMMIT = 'IGNORE_COMMIT'
export const IGNORE_COMMIT_FILE = 'IGNORE_COMMIT_FILE'
export const UPDATE_START_TIME = 'UPDATE_START_TIME'
export const UPDATE_END_TIME = 'UPDATE_END_TIME'
export const UPDATE_GENERAL_WEIGHT = 'UPDATE_GENERAL_WEIGHT'
export const UPDATE_EXTENSION = 'UPDATE_EXTENSION'
export const GET_PROJECT = 'GET_PROJECT'

interface setDateAction {
  type: typeof UPDATE_START_TIME | typeof UPDATE_END_TIME
  date: Date
}

interface ignoreMrAction {
  type: typeof IGNORE_MR
  mrId: number
  setIgnored: boolean
}

interface ignoreMrFileAction {
  type: typeof IGNORE_MR_FILE
  mrId: number
  fileId: string
  setIgnored: boolean
}

interface ignoreCommitAction {
  type: typeof IGNORE_COMMIT
  mrId: number
  commitId: string
  setIgnored: boolean
}

interface ignoreCommitFileAction {
  type: typeof IGNORE_COMMIT_FILE
  mrId: number
  commitId: string
  fileId: string
  setIgnored: boolean
}

export type TLocCategory =
  | 'syntaxes'
  | 'whitespaces'
  | 'deletions'
  | 'additions'
  | 'comments'

interface updateGeneralWeightAction {
  type: typeof UPDATE_GENERAL_WEIGHT
  category: TLocCategory
  newWeight: number
}

interface updateExtensionWeightAction {
  type: typeof UPDATE_EXTENSION
  extension: string
  newWeight: number
}

interface getProjectAction {
  type: typeof GET_PROJECT
  projectId: number
  config: IUserConfig
}

export type TProjectActions =
  | setDateAction
  | ignoreMrAction
  | ignoreMrFileAction
  | ignoreCommitAction
  | ignoreCommitFileAction
  | updateGeneralWeightAction
  | updateExtensionWeightAction
  | getProjectAction

export interface ILoc {
  syntaxes: number
  whitespaces: number
  comments: number
  additions: number
  deletions: number
}

interface IFile extends IFileData {
  loc: ILoc
  scores: ILoc
  score: number
}

export type TFiles = {
  [fileId: string]: IFile
}

export interface ICommit extends Omit<ICommitData, 'files'> {
  // loc: ILoc
  // scores: ILoc
  score: number
  files: TFiles
}

export type TCommits = {
  [commitId: string]: ICommit
}

export interface ISumOfCommitsScore {
  [memberId: number]: number
}

export interface IMergeRequest
  extends Omit<IMergeData, 'commits' | 'files' | 'sumOfCommitsScore'> {
  // loc: ILoc // is this needed?
  // scores: ILoc
  // commitScore: number
  numAdditions: number
  numDeletions: number
  score: number
  sumOfCommitsScore: ISumOfCommitsScore
  commits: TCommits
  files: TFiles
}

export type TMergeRequests = {
  [mergeRequestId: number]: IMergeRequest
}

export interface IMember extends IMemberData {
  soloMrScore: number
  sharedMrScore: number
  commitScore: number
  numDeletions: number
  numAdditions: number
  numCommits: number
  wordCount: number
  numComments: number
  mergeRequests: TMergeRequests
}

export type TMembers = {
  [memberId: number]: IMember
}

export interface IProjectState {
  mergeRequests: TMergeRequests
  members: TMembers
  id: number
}

export type TProjectState = IProjectState | undefined

export type TProjectReducer = (
  state: TProjectState,
  action: TProjectActions
) => Promise<TProjectState>

export interface IProjectContext {
  project: TProjectState
  dispatch: React.Dispatch<TProjectActions>
}
