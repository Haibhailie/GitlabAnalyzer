package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommitterController {
    private final CommitterService committerService;
    private final AuthenticationService authService;
    private static final Gson gson = new Gson();

    @Autowired
    public CommitterController(CommitterService committerService, AuthenticationService authService) {
        this.committerService = committerService;
        this.authService = authService;
    }

    @GetMapping("/api/project/{projectId}/committers")
    public String getCommitterTableForAProject(@CookieValue(value = "sessionId") String jwt,
                                                         @PathVariable("projectId") int projectId,
                                                         HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            return tryGettingCommitterTableJson(jwt, projectId, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
            return gson.toJson(new ArrayList<>());
        }
    }

    private String tryGettingCommitterTableJson(String jwt, int projectId, HttpServletResponse response) {
        try {
            Optional<List<CommitterDto>> committers = committerService.getCommittersInProject(jwt, projectId);
            response.setStatus(committers.isEmpty() ? SC_NOT_FOUND : SC_OK);
            return gson.toJson(committers.orElse(new ArrayList<>()));
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return "";
        }
    }

}
