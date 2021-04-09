import { createContext, useReducer } from 'react'
import { IProjectData } from '../types'

export type IProjectReducerAction = {
  type: 'SET_PROJECT'
  project: IProjectData | undefined
}

export type IProjectReducer = (
  state: IProjectData,
  action: IProjectReducerAction
) => IProjectData

export interface IContext {
  project: IProjectData
  dispatch: React.Dispatch<IProjectReducerAction>
}

const initialState: IProjectData = {
  id: null,
  name: '',
  members: [],
  numBranches: 0,
  numCommits: 0,
  repoSize: 0,
  createdAt: 0,
}

export const ProjectContext = createContext<IContext>({
  project: initialState,
  dispatch: (value: IProjectReducerAction) => console.log(value),
})

const reducer: IProjectReducer = (state, { type, project }) => {
  switch (type) {
    case 'SET_PROJECT':
      return {
        ...state,
        ...project,
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
  const [project, dispatch] = useReducer(reducer, initialState)
  return (
    <ProjectContext.Provider value={{ project, dispatch }}>
      {children}
    </ProjectContext.Provider>
  )
}

export default ProjectProvider
