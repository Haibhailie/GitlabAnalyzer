export type TCommitData = ICommitData[]
export interface ICommitData {
  sha: string
  author: string
  time: number
  score: number
  title: string
  id?: string
}

export type TMergeData = IMergeData[]
export interface IMergeData {
  id: string
  author: string
  time: number
  title: string
  score: number
  // TODO: remove mergeRequestId after BE fix.
  mergeRequestId?: string
}

export type TMemberData = IMemberData[]
export interface IMemberData {
  id: string
  username: string
  displayName: string
  role: string
  webUrl: string
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
