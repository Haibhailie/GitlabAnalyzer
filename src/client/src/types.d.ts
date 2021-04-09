export type TLineType =
  | 'HEADER'
  | 'ADDITION'
  | 'DELETION'
  | 'LINE_NUMBER_SPECIFICATION'
  | 'ADDITION_BLANK'

export type TDiffData = IDiffData[]
export interface IDiffData {
  diffLine: string
  lineType: TLineType
}

export interface IScore {
  totalScore: number
  scoreAdditions: number
  scoreDeletions: number
  scoreBlankAdditions: number
  scoreSyntaxChanges: number
}

export interface ILocChanges {
  numAdditions: number
  numDeletions: number
  numBlankAdditions: number
  numSyntaxChanges: number
  numSpacingChanges: number
}

export type TFileData = IFileData[]
export interface IFileData {
  name: string
  extension: string
  fileDiffs: TDiffData
  fileScore: IScore
  isIgnored: boolean
  linesOfCodeChanges: ILocChanges
}

export type TCommitData = ICommitData[]
export interface ICommitData {
  author: string
  authorEmail: string
  diffs: string
  files: TFileData
  id: string
  isIgnored: boolean
  message: string
  numAdditions: number
  numDeletions: number
  time: number
  title: string
  total: number
  webUrl: string
  score: number
}

export type TCommitDiffs = {
  fileScore: IScore
  linesOfCodeChanges: ILocChanges
}[]

export type TMergeData = IMergeData[]
export interface IMergeData {
  mergeRequestId: string
  author: string
  commitsInfoInMergeRequest: TCommitDiffs
  committers: string[]
  description: string
  files: TFileData
  hasConflicts: boolean
  isIgnored: boolean
  isOpen: boolean
  mergeRequestId: number
  participants: TMemberData
  sourceBranch: string
  sumOfCommitsScore: number
  targetBranch: string
  time: number
  title: string
  userId: number
  webUrl: string
  score: number
}

export type TMemberData = IMemberData[]
export interface IMemberData {
  id: string
  username: string
  displayName: string
  role: string
}

export interface IDiffData {
  diff: string
}
