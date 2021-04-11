package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

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
        List<ProjectDtoDb> projects = projectService.getAllProjects(jwt);
        response.setStatus(projects == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(projects);
    }

    @GetMapping(path = "/api/project/{projectId}")
    public String getProject(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable("projectId") int projectId,
                             HttpServletResponse response) {
        Optional<ProjectDtoDb> project = projectService.getProject(jwt, projectId);
        response.setStatus(project.isEmpty() ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(project);
    }

}