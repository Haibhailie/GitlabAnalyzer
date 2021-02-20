package ca.sfu.orcus.gitlabanalyzer.commit;


import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.utils.ISO8601;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommitController {
    private static final long EPOCH_TO_DATE_FACTOR = 1000;
    private static final String BEFORE_GITLAB_EXISTED = "2013-01-01T00:00:00Z";
    private static final long DEFAULT_SINCE = 0;
    private static final long DEFAULT_UNTIL = -1;
    private boolean parseExceptionThrown = false;

    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/api/project/{projectId}/commits")
    public String getCommits(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable int projectId,
                             @RequestParam (required = false, defaultValue = "0") long since,
                             @RequestParam (required = false, defaultValue = "-1") long until,
                             HttpServletResponse response) {
        Date dateSince = getDateSince(since);
        Date dateUntil = getDateUntil(until);
        Gson gson = new Gson();

        if(parseExceptionThrown) {
            response.setStatus(400);
            return gson.toJson(null);
        }

        List<CommitDTO> commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);

        response.setStatus(commits == null ? 401 : 200);
        return gson.toJson(commits);
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}")
    public String getSingleCommit(@CookieValue(value = "sessionId") String jwt,
                                  @PathVariable int projectId,
                                  @PathVariable String sha,
                                  HttpServletResponse response) throws GitLabApiException {
        CommitDTO commit = commitService.getSingleCommit(jwt, projectId, sha);
        response.setStatus(commit == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commit);
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}/diff")
    public String getSingleCommitDiffs(@CookieValue(value = "sessionId") String jwt,
                                       @PathVariable int projectId,
                                       @PathVariable String sha,
                                       HttpServletResponse response) throws GitLabApiException {
        List<Diff> diffs = commitService.getDiffOfCommit(jwt, projectId, sha);
        response.setStatus(diffs == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(diffs);
    }

    private Date getDateSince(long since) {
        if(since != DEFAULT_SINCE) {
            return new Date(since * EPOCH_TO_DATE_FACTOR); // since given value
        } else {
            try {
                return ISO8601.toDate(BEFORE_GITLAB_EXISTED);  // since 2013
            } catch(ParseException e) {
                parseExceptionThrown = true;
            }
        }
        return new Date(0);
    }

    private Date getDateUntil(long until) {
        if(until != DEFAULT_UNTIL) {
            return new Date(until * EPOCH_TO_DATE_FACTOR); // until given value
        } else {
            return new Date();                             // until now
        }
    }
}
