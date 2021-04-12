import { IFileData, TMemberData, TMergeData } from '../../types'
import jsonFetcher from '../../utils/jsonFetcher'
import { IGeneralTypeScoring, IUserConfig } from '../UserConfigContext'
import {
  IGNORE_COMMIT,
  IGNORE_COMMIT_FILE,
  IGNORE_MR,
  IGNORE_MR_FILE,
  UPDATE_TIME,
  UPDATE_GENERAL_WEIGHT,
  TProjectReducer,
  IProjectState,
  ILoc,
  GET_PROJECT,
  TFiles,
  TCommits,
  TLocCategory,
  ISumOfCommitsScore,
  TMergeRequests,
  TMembers,
} from './types'
import objectEquals from 'fast-deep-equal'

const addOrSub = (isCurrentlyIgnored: boolean) => (isCurrentlyIgnored ? 1 : -1)

const calcScore = (scores: ILoc) =>
  Object.values(scores).reduce((accum, score) => accum + score, 0)

const weightReverseMap: Record<string, TLocCategory> = {
  'New line of code': 'additions',
  'Deleting a line': 'deletions',
  'Comment/blank': 'comments',
  'Spacing change': 'whitespaces',
  'Syntax only': 'syntaxes',
}

const translateWeights = (weights: IGeneralTypeScoring[]) => {
  const weightMap: Record<string, number> = {}

  weights.forEach(({ type, value }) => {
    if (type in weightReverseMap) weightMap[weightReverseMap[type]] = value
  })

  return weightMap
}

const calcScores = (loc: ILoc, weights: IGeneralTypeScoring[]): ILoc => {
  const weightMap = translateWeights(weights)
  return {
    additions: loc.additions * (weightMap.additions ?? 1),
    deletions: loc.deletions * (weightMap.deletions ?? 0.2),
    comments: loc.comments * (weightMap.comments ?? 0),
    syntaxes: loc.syntaxes * (weightMap.syntaxes ?? 0.2),
    whitespaces: loc.whitespaces * (weightMap.whitespaces ?? 0),
  }
}

const ignoreFile = (
  state: IProjectState,
  mrId: number,
  fileId: string,
  setIgnored: boolean,
  commitId?: string
) => {
  const mr = state.mergeRequests[mrId]
  const commit = !!commitId && mr.commits[commitId]
  const file = commit ? commit.files[fileId] : mr.files[fileId]
  const member = state.members[commit ? commit.userId : mr.userId]
  if (!mr || !file || !member || file.isIgnored === setIgnored) return state

  const scoreDelta = addOrSub(!setIgnored) * file.score

  if (!setIgnored) {
    mr.isIgnored = false
  }

  file.isIgnored = setIgnored

  if (commit) {
    commit.isIgnored = commit.isIgnored && setIgnored
    commit.score += scoreDelta
    mr.sumOfCommitsScore[member.id] += scoreDelta
  } else {
    mr.score += scoreDelta
  }

  return { ...state }
}

const ignoreCommit = (
  state: IProjectState,
  mrId: number,
  commitId: string,
  setIgnored: boolean
) => {
  const commit = state.mergeRequests[mrId]?.commits[commitId]
  if (!commit || commit.isIgnored === setIgnored) return state

  Object.keys(commit.files).forEach(fileId => {
    state = ignoreFile(state, mrId, fileId, setIgnored, commitId)
  })

  commit.isIgnored = setIgnored

  return state
}

const ignoreMr = (state: IProjectState, mrId: number, setIgnored: boolean) => {
  const mr = state?.mergeRequests[mrId]
  if (!mr || mr.isIgnored === setIgnored || !state?.id) return state

  Object.keys(mr.commits).forEach(commitId => {
    state = ignoreCommit(state, mrId, commitId, setIgnored)
  })

  Object.keys(mr.files).forEach(fileId => {
    state = ignoreFile(state, mrId, fileId, setIgnored)
  })

  mr.isIgnored = setIgnored

  // await jsonFetcher(`/api/project/${state.id}/mergerequest/${mrId}/ignore`)

  return state
}

let currentProject: number
let currentConfig: IUserConfig

const getFileLoc = (file: IFileData): ILoc => {
  return {
    additions: file.linesOfCodeChanges.numAdditions,
    deletions: file.linesOfCodeChanges.numDeletions,
    comments: file.linesOfCodeChanges.numBlankAdditions,
    syntaxes: file.linesOfCodeChanges.numSyntaxChanges,
    whitespaces: file.linesOfCodeChanges.numSpacingChanges,
  }
}

const formatMergeRequests = (
  mergeRequests: TMergeData,
  config: IUserConfig
) => {
  const formattedMrs: TMergeRequests = {}

  mergeRequests.forEach(mr => {
    const commits: TCommits = {}
    const mrCommitsScore: ISumOfCommitsScore = {}

    mr.commits.forEach(commit => {
      const files: TFiles = {}
      let commitScore = 0

      commit.files.forEach(file => {
        file.id = file.id ?? file.name

        const loc: ILoc = getFileLoc(file)

        const scores = calcScores(loc, config.generalScores)
        const score = calcScore(scores)

        if (!file.isIgnored) commitScore += score

        files[file.id] = {
          ...file,
          loc,
          scores,
          score,
        }
      })

      if (!commit.isIgnored) mrCommitsScore[commit.userId] += commitScore

      commits[commit.id] = {
        ...commit,
        files,
        score: commitScore,
        mrId: mr.mergeRequestId,
      }
    })

    const mrFiles: TFiles = {}
    let mrScore = 0
    let numDeletions = 0
    let numAdditions = 0

    mr.files.forEach(file => {
      file.id = file.id ?? file.name
      const loc: ILoc = getFileLoc(file)

      const scores = calcScores(loc, config.generalScores)
      const score = calcScore(scores)

      const {
        numAdditions: additions,
        numBlankAdditions: blanks,
        numDeletions: deletions,
        numSpacingChanges: spaces,
        numSyntaxChanges: syntaxes,
      } = file.linesOfCodeChanges

      numDeletions += deletions
      numAdditions += additions + blanks + spaces + syntaxes

      if (!file.isIgnored) mrScore += score

      mrFiles[file.id] = {
        ...file,
        loc,
        scores,
        score,
      }
    })

    formattedMrs[mr.mergeRequestId] = {
      ...mr,
      commits,
      sumOfCommitsScore: mrCommitsScore,
      numAdditions,
      numDeletions,
      files: mrFiles,
      score: mrScore,
    }
  })

  return formattedMrs
}

const formatMembers = (members: TMemberData, mrs: TMergeRequests) => {
  const formattedMembers: TMembers = {}

  members.forEach(member => {
    let wordCount = 0
    member.notes.forEach(note => {
      wordCount += note.wordCount
    })

    formattedMembers[member.id] = {
      ...member,
      soloMrScore: 0,
      sharedMrScore: 0,
      commitScore: 0,
      numAdditions: 0,
      numDeletions: 0,
      numCommits: 0,
      wordCount,
      numComments: member.notes.length,
      mergeRequests: {},
    }
  })

  Object.values(mrs).forEach(mr => {
    const member = formattedMembers[mr.userId]
    if (!member) return

    if (!mr.isIgnored) {
      if (mr.isSolo) {
        member.soloMrScore += mr.score
      } else {
        member.sharedMrScore += mr.sumOfCommitsScore[member.id] ?? 0
      }

      Object.values(mr.commits).forEach(commit => {
        if (!commit.isIgnored) {
          member.commitScore += commit.score
          member.numCommits++
        }
      })

      member.numAdditions += mr.numAdditions
      member.numDeletions += mr.numDeletions
    }

    member.mergeRequests[mr.mergeRequestId] = mr
  })

  return formattedMembers
}

const reducer: TProjectReducer = async (state, action) => {
  if (action.type === GET_PROJECT) {
    const { projectId, config } = action

    if (
      projectId === currentProject &&
      objectEquals(currentConfig, config) &&
      state
    )
      return state

    currentProject = projectId
    currentConfig = config

    let mergeRequests
    let members

    try {
      mergeRequests = await jsonFetcher<TMergeData>(
        `/api/project/${projectId}/mergerequests`
      )
      members = await jsonFetcher<TMemberData>(
        `/api/project/${projectId}/members`
      )
    } catch {
      return state
    }

    const formattedMrs = formatMergeRequests(mergeRequests, config)
    const formattedMembers = formatMembers(members, formattedMrs)

    return {
      mergeRequests: formattedMrs,
      members: formattedMembers,
      id: projectId,
    }
  }

  if (!state) return state

  switch (action.type) {
    case UPDATE_TIME: {
      const { startDate, endDate } = action
      const startTime = startDate.getTime()
      const endTime = endDate.getTime()
      Object.values(state.members).forEach(member => {
        member.wordCount = 0
        member.numComments = 0
        member.mergeRequests = {}
        member.soloMrScore = 0
        member.sharedMrScore = 0
        member.commitScore = 0
        member.numCommits = 0
        member.numAdditions = 0
        member.numDeletions = 0
        member.notes.forEach(note => {
          if (note.date >= startTime && note.date <= endTime) {
            member.numComments++
            member.wordCount += note.wordCount
          }
        })
      })

      Object.values(state.mergeRequests).forEach(mr => {
        const member = state.members[mr.userId]
        if (!member) return

        if (!mr.isIgnored && mr.time >= startTime && mr.time <= endTime) {
          if (mr.isSolo) {
            member.soloMrScore += mr.score
          } else {
            member.sharedMrScore += mr.sumOfCommitsScore[member.id] ?? 0
          }

          Object.values(mr.commits).forEach(commit => {
            if (!commit.isIgnored) {
              member.commitScore += commit.score
              member.numCommits++
            }
          })

          member.numAdditions += mr.numAdditions
          member.numDeletions += mr.numDeletions
          member.mergeRequests[mr.mergeRequestId] = mr
        }
      })
      return { ...state }
    }
    case IGNORE_MR: {
      const { mrId, setIgnored } = action
      return ignoreMr(state, mrId, setIgnored)
    }
    case IGNORE_MR_FILE: {
      const { mrId, fileId, setIgnored } = action
      return ignoreFile(state, mrId, fileId, setIgnored)
    }
    case IGNORE_COMMIT: {
      const { mrId, commitId, setIgnored } = action
      return ignoreCommit(state, mrId, commitId, setIgnored)
    }
    case IGNORE_COMMIT_FILE: {
      const { mrId, commitId, setIgnored, fileId } = action
      return ignoreFile(state, mrId, fileId, setIgnored, commitId)
    }
    case UPDATE_GENERAL_WEIGHT: {
      Object.values(state.mergeRequests).forEach(mr => {
        mr.score = 0
        Object.values(mr.files).forEach(file => {
          file.scores = calcScores(file.loc, action.weights)
          const newScore = calcScore(file.scores)
          file.score = newScore
          mr.score += newScore
        })
        Object.values(mr.commits).forEach(commit => {
          mr.sumOfCommitsScore[commit.userId] = 0
          commit.score = 0
          Object.values(commit.files).forEach(file => {
            file.scores = calcScores(file.loc, action.weights)
            const newScore = calcScore(file.scores)
            file.score = newScore
            mr.sumOfCommitsScore[commit.userId] += newScore
            commit.score += newScore
          })
        })
      })
      return { ...state }
    }
    default: {
      return state
    }
  }
}

export default reducer
