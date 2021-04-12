import { useContext } from 'react'
import { GET_PROJECT, ProjectContext } from '../context/ProjectContext'
import { UserConfigContext } from '../context/UserConfigContext'

const useProject = (projectId?: number) => {
  const { dispatch, project } = useContext(ProjectContext)
  const { userConfigs } = useContext(UserConfigContext)

  if (projectId && (!project || projectId != project.id)) {
    dispatch({
      type: GET_PROJECT,
      config: userConfigs.selected,
      projectId,
    })

    return 'LOADING'
  }

  return project
}

export default useProject
