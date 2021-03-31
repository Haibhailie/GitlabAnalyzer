package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import java.lang.reflect.Type;
import java.util.*;

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

    @PostMapping("/api/project/{projectId}/committers")
    public void updateCommitterResolution(@CookieValue(value = "sessionId") String jwt,
                                          @PathVariable("projectId") int projectId,
                                          @RequestBody String committers,
                                          HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            tryUpdatingCommitterTable(jwt, projectId, committers, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
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

    private Map<String, Integer> getCommitterToMemberMap(String committers) {
        Type typeOfCommitterList = new TypeToken<ArrayList<CommitterToMemberMapping>>() {}.getType();
        List<CommitterToMemberMapping> committerList = gson.fromJson(committers, typeOfCommitterList);
        Map<String, Integer> committerToMemberMap = new HashMap<>();
        for (CommitterToMemberMapping o : committerList) {
            committerToMemberMap.put(o.name, o.memberId);
        }
        return committerToMemberMap;
    }

    private void tryUpdatingCommitterTable(String jwt, int projectId, String committers, HttpServletResponse response) {
        Map<String, Integer> committerToMemberMap = getCommitterToMemberMap(committers);
        try {
            committerService.updateCommitterTable(jwt, projectId, committerToMemberMap);
            response.setStatus(SC_OK);
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            response.setStatus(SC_NOT_FOUND);
        }
    }

    private static class CommitterToMemberMapping {
        public String name;
        public int memberId;
    }
}
