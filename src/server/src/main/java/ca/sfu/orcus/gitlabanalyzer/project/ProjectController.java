package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api")
public class ProjectController {
    private final ProjectService projectService;

    // TODO: Need to provide GitLabApi after authentication is set up
    private final GitLabApi gitLabApi = GitLabApi.oauth2Login("http://cmpt373-1211-09.cmpt.sfu.ca",
            "user",
            "pass");

    @Autowired
    public ProjectController(ProjectService projectService) throws GitLabApiException {
        this.projectService = projectService;
    }

    @GetMapping(path = "projects")
    public List<ProjectDto> getAllProjects() throws GitLabApiException {
        return projectService.getAllProjects(gitLabApi);
    }

    @GetMapping(path = "core/{projectId}/project")
    public ProjectDto getProject(@PathVariable("projectId") int projectId) throws GitLabApiException {
        return projectService.getProject(gitLabApi, projectId);
    }
}