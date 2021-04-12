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
      if (action.date < (state.selected.endDate ?? new Date())) {
        try {
          await jsonFetcher('/api/config/current', {
            method: 'PUT',
            responseIsEmpty: true,
            body: JSON.stringify({
              ...state,
              selected: {
                ...state.selected,
                startDate: action.date?.getTime() ?? Date.now(),
              },
            }),
          })
          return {
            ...state,
            selected: {
              ...state.selected,
              startDate: action.date,
            },
          }
        } catch {
          return state
        }
      }
      return state
    case SET_END_DATE:
      if (action.date > (state.selected.startDate ?? dateZero)) {
        try {
          await jsonFetcher('/api/config/current', {
            method: 'PUT',
            responseIsEmpty: true,
            body: JSON.stringify({
              ...state,
              selected: {
                ...state.selected,
                endDate: action.date?.getTime() ?? Date.now(),
              },
            }),
          })
          return {
            ...state,
            selected: {
              ...state.selected,
              endDate: action.date,
            },
          }
        } catch {
          return state
        }
      }
      return state
    case SET_SCORE_BY:
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: {
              ...state.selected,
              scoreBy: action.scoreBy,
            },
          }),
        })
        return {
          ...state,
          selected: {
            ...state.selected,
            scoreBy: action.scoreBy,
          },
        }
      } catch {
        return state
      }
    case SET_GRAPH_Y_AXIS:
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: {
              ...state.selected,
              yAxis: action.yAxis,
            },
          }),
        })
        return {
          ...state,
          selected: {
            ...state.selected,
            yAxis: action.yAxis,
          },
        }
      } catch {
        return state
      }

    case SET_GRAPH_BY:
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: {
              ...state.selected,
              graphMode: action.graphMode,
            },
          }),
        })
        return {
          ...state,
          selected: {
            ...state.selected,
            graphMode: action.graphMode,
          },
        }
      } catch {
        return state
      }

    case SET_CONFIG_NAME:
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: {
              ...state.selected,
              name: action.name,
            },
          }),
        })
        return {
          ...state,
          selected: {
            ...state.selected,
            name: action.name,
          },
        }
      } catch {
        return state
      }

    case SET_SCORES:
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: {
              ...state.selected,
              fileScores: action.scores.fileScores,
              generalScores: action.scores.generalScores,
            },
          }),
        })
        return {
          ...state,
          selected: {
            ...state.selected,
            fileScores: action.scores.fileScores,
            generalScores: action.scores.generalScores,
          },
        }
      } catch {
        return state
      }

    case SET_CONFIG:
      if (!state.configs[action.id]) return state
      try {
        await jsonFetcher('/api/config/current', {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            ...state,
            selected: state.configs[action.id],
          }),
        })
        return {
          ...state,
          selected: state.configs[action.id],
        }
      } catch {
        return state
      }

    case UPDATE_CONFIG:
      if (!state.configs[action.id]) return state
      try {
        await jsonFetcher(`/api/config/${action.id}`, {
          method: 'PUT',
          responseIsEmpty: true,
          body: JSON.stringify({
            configs: {
              ...state.configs,
              [action.id]: {
                ...state.selected,
              },
              selected: {
                ...state.selected,
              },
            },
          }),
        })
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
      } catch {
        return state
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

        let currConfig = await jsonFetcher<IUserConfig>('/api/config/current')
        currConfig = {
          ...currConfig,
          startDate:
            currConfig.startDate !== undefined
              ? new Date(currConfig.startDate)
              : new Date(0),
          endDate:
            currConfig.endDate !== undefined
              ? new Date(currConfig.endDate)
              : new Date(),
        }
        return {
          ...state,
          selected: { ...currConfig },
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
