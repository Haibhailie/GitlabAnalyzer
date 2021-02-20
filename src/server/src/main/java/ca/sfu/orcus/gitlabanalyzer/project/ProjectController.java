package ca.sfu.orcus.gitlabanalyzer.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(path = "api/projects")
    public String getAllProjects(@CookieValue(value = "sessionId") String jwt,
                                 HttpServletResponse response) {
        List<ProjectDto> projects = projectService.getAllProjects(jwt);
        response.setStatus(projects == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(projects);
    }

    @GetMapping(path = "api/project/{projectId}")
    public String getProject(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable("projectId") int projectId,
                             HttpServletResponse response) {
        ProjectExtendedDto project = projectService.getProject(jwt, projectId);
        response.setStatus(project == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(project);
    }
}