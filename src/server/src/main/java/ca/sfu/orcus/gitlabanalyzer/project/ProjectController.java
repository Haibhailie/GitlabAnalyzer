package ca.sfu.orcus.gitlabanalyzer.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProjectController {
    private final ProjectService projectService;
    private static final Gson gson = new Gson();

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(path = "/api/projects")
    public String getAllProjects(@CookieValue(value = "sessionId") String jwt,
                                 HttpServletResponse response) {
        List<ProjectDto> projects = projectService.getAllProjects(jwt);
        response.setStatus(projects == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(projects);
    }

    @GetMapping(path = "/api/project/{projectId}")
    public String getProject(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable("projectId") int projectId,
                             HttpServletResponse response) {
        ProjectExtendedDto project = projectService.getProject(jwt, projectId);
        response.setStatus(project == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(project);
    }
}