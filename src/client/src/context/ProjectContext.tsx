import { createContext, useReducer } from 'react'

export interface IProjectData {
  id: string | null
}

export type IProjectReducerAction = {
  type: 'SET_ID'
  id: string | null
}

export type IProjectReducer = (
  state: IProjectData,
  action: IProjectReducerAction
) => IProjectData

export interface IContext {
  project: IProjectData
  dispatch: React.Dispatch<IProjectReducerAction>
}

export const ProjectContext = createContext<IContext>({
  project: { id: null },
  dispatch: (value: IProjectReducerAction) => console.log(value),
})

const reducer: IProjectReducer = (state, { type, id }) => {
  switch (type) {
    case 'SET_ID':
      return {
        ...state,
        id,
      }
    default:
      return state
  }
}

const ProjectProvider = ({
  children,
}: {
  children: JSX.Element[] | JSX.Element
}) => {
  const [project, dispatch] = useReducer(reducer, { id: null })
  return (
    <ProjectContext.Provider value={{ project, dispatch }}>
      {children}
    </ProjectContext.Provider>
  )
}

export default ProjectProvider
