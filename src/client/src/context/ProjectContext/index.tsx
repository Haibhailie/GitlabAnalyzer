import { noop } from 'lodash'
import { createContext, ReactNode } from 'react'
import { IProjectContext, TProjectActions } from './types'
import reducer from './reducer'
import useAsyncReducer from '../../utils/useAsyncReducer'

export * from './types'

export const ProjectContext = createContext<IProjectContext>({
  project: undefined,
  dispatch: async (value: TProjectActions) => noop(value),
})

export type TProjectProvider = (props: { children: ReactNode }) => JSX.Element

const ProjectProvider: TProjectProvider = ({ children }) => {
  const [project, dispatch] = useAsyncReducer(reducer, undefined)
  return (
    <ProjectContext.Provider value={{ project, dispatch }}>
      {children}
    </ProjectContext.Provider>
  )
}

export default ProjectProvider
