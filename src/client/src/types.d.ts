export type TLineType =
  | 'HEADER'
  | 'ADDITION'
  | 'ADDITION_SYNTAX'
  | 'ADDITION_BLANK'
  | 'DELETION'
  | 'DELETION_SYNTAX'
  | 'DELETION_BLANK'
  | 'UNCHANGED'

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
  id: string
  extension: string
  fileDiffs: TDiffData
  fileScore: IScore
  isIgnored: boolean
  linesOfCodeChanges: ILocChanges
}

export type TCommitData = ICommitData[]
export interface ICommitData {
  id: string
  message: string
  author: string
  authorEmail: string
  userId: number
  time: number
  webUrl: string
  numAdditions: number
  numDeletions: number
  total: number
  diffs: string
  isIgnored: boolean
  files: TFileData
}

export type TCommitDiffs = {
  fileScore: IScore
  linesOfCodeChanges: ILocChanges
}[]

export type TMergeData = IMergeData[]
export interface IMergeData {
  mergeRequestId: number
  title: string
  author: string
  userId: number
  description: string
  time: number
  webUrl: string
  commits: TCommitData
  committerNames: string[]
  sumOfCommitsScore: number
  isIgnored: boolean
  files: TFileData
  isSolo: boolean
}

export type TCommentData = ICommentData[]
export interface ICommentData {
  id: number
  content: string
  wordCount: number
  date: number
  context: 'Issue' | 'MergeRequest'
  webUrl: string
  parentAuthor: string
}

export type TMemberData = IMemberData[]
export interface IMemberData {
  id: number
  displayName: string
  username: string
  role: string
  webUrl: string
  committerEmails: string[]
  mergeRequestDocIds: string[]
  notes: TCommentData
}

export type TCommitterData = ICommitterData[]
export interface ICommitterData {
  email: string
  name: string
  member: IMemberData
}

export type TMemberCommitterMap = IMemberCommitterMap[]
export interface IMemberCommitterMap {
  email: string
  memberId: number | string
}

export interface IDiffData {
  diff: string
}

export type TCommentData = ICommentData[]
export interface ICommentData {
  id: string
  wordcount: number
  content: string
  date: number
  context: string
  webUrl: string
  parentAuthor: string
}

export type TProjectData = IProjectData[]
export interface IProjectData {
  id: string | null
  name: string
  members: TMemberData
  numBranches: number
  numCommits: number
  repoSize: number
  createdAt: number
}
