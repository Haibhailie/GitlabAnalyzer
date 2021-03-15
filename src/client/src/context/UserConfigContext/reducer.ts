import jsonFetcher from '../../utils/jsonFetcher'
import {
  ADD_CONFIG,
  DELETE_CONFIG,
  UPDATE_CONFIG,
  SET_CONFIG_NAME,
  SET_END_DATE,
  SET_GRAPH_BY,
  SET_GRAPH_Y_AXIS,
  SET_SCORES,
  SET_SCORE_BY,
  SET_START_DATE,
  SET_CONFIG,
  TUserConfigReducer,
  FLUSH_CONFIGS,
  IUserConfig,
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
    case UPDATE_CONFIG:
      if (!state.configs[action.id]) return state

      return {
        configs: {
          ...state.configs,
          [action.id]: {
            ...state.selected,
          },
        },
        selected: {
          ...state.selected,
        },
      }
    case ADD_CONFIG:
      try {
        const newConfig = { ...state.selected }
        newConfig.name = action.name
        const { id } = await jsonFetcher<{ id: string }>(`/api/config`, {
          method: 'POST',
          body: JSON.stringify({
            ...newConfig,
            startDate: newConfig.startDate?.getTime(),
            // TODO: remove ?? edge case after BE fix.
            endDate: newConfig.endDate?.getTime() ?? Date.now(),
          }),
        })
        newConfig.id = id
        state.configs[id] = newConfig
        return {
          ...state,
          selected: { ...newConfig },
        }
      } catch {
        return state
      }
      break
    case DELETE_CONFIG:
      if (!action.id || !state.configs[action.id] || action.id === 'selected')
        return state

      try {
        await jsonFetcher(`/api/config/${action.id}`, {
          method: 'DELETE',
          responseIsEmpty: true,
        })
        delete state.configs[action.id]
        return { ...state }
      } catch {
        return state
      }
      break
    case FLUSH_CONFIGS:
      try {
        const configArr = await jsonFetcher<IUserConfig[]>('/api/configs')
        const configs: Record<string, IUserConfig> = {}
        configArr.forEach(config => {
          if (config.id) {
            configs[config.id] = {
              ...config,
              startDate:
                config.startDate !== undefined
                  ? new Date(config.startDate)
                  : new Date(0),
              endDate:
                config.endDate !== undefined
                  ? new Date(config.endDate)
                  : new Date(),
            }
          }
        })
        return {
          ...state,
          configs: { ...configs },
        }
      } catch {
        return state
      }
    default:
      return state
  }
}

export default reducer
