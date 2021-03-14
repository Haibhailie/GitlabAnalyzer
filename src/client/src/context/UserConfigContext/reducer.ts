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

const reducer: TUserConfigReducer = async (state, action) => {
  switch (action.type) {
    case SET_START_DATE:
      return {
        ...state,
        selected: {
          ...state.selected,
          startdate: action.date,
        },
      }
    case SET_END_DATE:
      return {
        ...state,
        selected: {
          ...state.selected,
          endDate: action.date,
        },
      }
    case SET_SCORE_BY:
      return {
        ...state,
        selected: {
          ...state.selected,
          scoreBy: action.scoreBy,
        },
      }
    case SET_GRAPH_Y_AXIS:
      return {
        ...state,
        selected: {
          ...state.selected,
          yAxis: action.yAxis,
        },
      }
    case SET_GRAPH_BY:
      return {
        ...state,
        selected: {
          ...state.selected,
          graphMode: action.graphMode,
        },
      }
    case SET_CONFIG_NAME:
      return {
        ...state,
        selected: {
          ...state.selected,
          name: action.name,
        },
      }
    case SET_SCORES:
      return {
        ...state,
        selected: {
          ...state.selected,
          fileScores: action.scores.fileScores,
          generalScores: action.scores.generalScores,
        },
      }
    case SET_USER_CONFIG:
      return {
        ...state,
        selected: action.userConfig,
      }
    default:
      return state
  }
}

export default reducer
