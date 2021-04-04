package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final AuthenticationService authService;

    @Autowired
    public AnalysisController(AnalysisService analysisService, AuthenticationService authService) {
        this.analysisService = analysisService;
        this.authService = authService;
    }

    @PutMapping(value = "/api/{projectId}/analyze")
    public void analyzeProject(@CookieValue(value = "sessionId") String jwt,
                               @PathVariable("projectId") int projectId,
                               HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            tryAnalyzingProject(jwt, projectId, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    private void tryAnalyzingProject(String jwt, int projectId, HttpServletResponse response) {
        try {
            analysisService.analyzeProject(jwt, projectId);
            response.setStatus(SC_OK);
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}