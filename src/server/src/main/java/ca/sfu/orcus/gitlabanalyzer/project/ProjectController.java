package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    // TODO: Need to get the GitLabApi after authentication is completed
    private final GitLabApi gitLabApi = null;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects(gitLabApi);
    }

    @GetMapping(path = "{projectId")
    public ProjectDto getProject(@PathVariable("projectId") int projectId) {
        return projectService.getProject(gitLabApi, projectId);
    }
}