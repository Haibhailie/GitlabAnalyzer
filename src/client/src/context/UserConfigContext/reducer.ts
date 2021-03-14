import {
  SET_CONFIG_NAME,
  SET_END_DATE,
  SET_GRAPH_BY,
  SET_GRAPH_Y_AXIS,
  SET_SCORES,
  SET_SCORE_BY,
  SET_START_DATE,
  SET_USER_CONFIG,
  TUserConfigReducer,
} from './types'

const reducer: TUserConfigReducer = (state, action) => {
  switch (action.type) {
    case SET_START_DATE:
      return {
        ...state,
        startDate: action.date,
      }
    case SET_END_DATE:
      return {
        ...state,
        endDate: action.date,
      }
    case SET_SCORE_BY:
      return {
        ...state,
        scoreBy: action.scoreBy,
      }
    case SET_GRAPH_Y_AXIS:
      return {
        ...state,
        graphYAxis: action.yAxis,
      }
    case SET_GRAPH_BY:
      return {
        ...state,
        projectGraphBy: action.graphMode,
      }
    case SET_CONFIG_NAME:
      return {
        ...state,
        name: action.name,
      }
    case SET_SCORES:
      return {
        ...state,
        generalScores: action.scores.generalScores,
        fileScores: action.scores.fileScores,
      }
    case SET_USER_CONFIG:
      return {
        ...action.userConfig,
      }
    default:
      return state
  }
}

export default reducer
