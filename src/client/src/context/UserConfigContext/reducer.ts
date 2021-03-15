import jsonFetcher from '../../utils/jsonFetcher'
import {
  ADD_CONFIG,
  DELETE_CONFIG,
  SET_CONFIG_NAME,
  SET_END_DATE,
  SET_GRAPH_BY,
  SET_GRAPH_Y_AXIS,
  SET_SCORES,
  SET_SCORE_BY,
  SET_START_DATE,
  SET_CONFIG,
  TUserConfigReducer,
} from './types'

const dateZero = new Date(0)
const reducer: TUserConfigReducer = async (state, action) => {
  switch (action.type) {
    case SET_START_DATE:
      return action.date < (state.selected.endDate ?? new Date())
        ? {
            ...state,
            selected: {
              ...state.selected,
              startDate: action.date,
            },
          }
        : state
    case SET_END_DATE:
      return action.date > (state.selected.startDate ?? dateZero)
        ? {
            ...state,
            selected: {
              ...state.selected,
              endDate: action.date,
            },
          }
        : state
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
    case SET_CONFIG:
      if (!state.configs[action.id]) return state
      return {
        ...state,
        selected: state.configs[action.id],
      }
    case ADD_CONFIG:
      try {
        const { id } = await jsonFetcher<{ id: string }>(`/config`, {
          method: 'POST',
          body: JSON.stringify(action.config),
        })
        action.config.id = id
        state.configs[id] = action.config
        return {
          ...state,
          selected: action.config,
        }
      } catch {
        return state
      }
      break
    case DELETE_CONFIG:
      if (!action.id || !state.configs[action.id] || action.id === 'selected')
        return state

      try {
        await jsonFetcher(`/config/${action.id}`, {
          method: 'DELETE',
          responseIsEmpty: true,
        })
        delete state.configs[action.id]
        return { ...state }
      } catch {
        return state
      }
      break
    default:
      return state
  }
}

export default reducer
