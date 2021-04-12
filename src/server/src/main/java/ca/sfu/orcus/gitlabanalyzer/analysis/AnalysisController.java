package ca.sfu.orcus.gitlabanalyzer.analysis;

import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAuthorizedException;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AnalysisController {
    private final AnalysisService analysisService;

    @Autowired
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PutMapping(value = "/api/{projectId}/analyze")
    public void analyzeProject(@CookieValue(value = "sessionId") String jwt,
                               @PathVariable("projectId") int projectId,
                               HttpServletResponse response) {
        try {
            analysisService.analyzeProject(jwt, projectId);
            response.setStatus(SC_OK);
        } catch (NotAuthorizedException e) {
            response.setStatus(SC_UNAUTHORIZED);
        } catch (GitLabApiException | NullPointerException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}