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
}

export type TCommitterData = ICommitterData[]
export interface ICommitterData {
  email: string
  name: string
  memberDto: IMemberData
}

export type TMemberCommitterMap = IMemberCommitterMap[]
export interface IMemberCommitterMap {
  email: string
  memberId: number | string
}

export interface IDiffData {
  diff: string
}
