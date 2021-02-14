package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ArrayList<ProjectDto> getAllProjects(GitLabApi gitLabApi) throws GitLabApiException {
        ArrayList<ProjectDto> projectDtos = new ArrayList<>();
        List<Project> projects = gitLabApi.getProjectApi().getProjects();
        for (Project p: projects) {
            ProjectDto projectDto = new ProjectDto(gitLabApi, p);
            projectDtos.add(projectDto);
        }

        return projectDtos;
    }

    public ProjectDto getProject(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        Project project = gitLabApi.getProjectApi().getProject(projectId);
        ProjectDto projectDto = new ProjectDto(gitLabApi, project);

        return projectDto;
    }

}
