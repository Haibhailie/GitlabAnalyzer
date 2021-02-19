package ca.sfu.orcus.gitlabanalyzer.commit;


import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
public class CommitController {
    private final CommitService commitService;
    private static final long EPOCH_TO_DATE_FACTOR = 1000;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/api/core/{projectId}/commits")
    public String getCommits(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable int projectId,
                             @RequestParam (required = false) String since,
                             @RequestParam (required = false) String until,
                             HttpServletResponse response) throws GitLabApiException {
        List<CommitDTO> commits;
        Date dateSince;
        Date dateUntil;
        if(since != null) {
            dateSince = new Date(Long.parseLong(since) * EPOCH_TO_DATE_FACTOR); // since given value
            if(until != null) {
                dateUntil = new Date(Long.parseLong(until) * EPOCH_TO_DATE_FACTOR); // until given value
            } else {
                dateUntil = new Date();                                         // until now
            }
            commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        } else if(until != null) {
            dateSince = new Date(0);                                            // since 1969
            dateUntil = new Date(Long.parseLong(until) * EPOCH_TO_DATE_FACTOR); // until given value
            commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        } else {
            dateSince = new Date(0);                            // since 1969
            dateUntil = new Date();                             // until now
            commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        }
        response.setStatus(commits == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commits);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}")
    public String getSingleCommit(@CookieValue(value = "sessionId") String jwt,
                                  @PathVariable int projectId,
                                  @PathVariable String sha,
                                  HttpServletResponse response) throws GitLabApiException {
        CommitDTO commit = commitService.getSingleCommit(jwt, projectId, sha);
        response.setStatus(commit == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commit);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}/diff")
    public String getSingleCommitDiffs(@CookieValue(value = "sessionId") String jwt,
                                       @PathVariable int projectId,
                                       @PathVariable String sha,
                                       HttpServletResponse response) throws GitLabApiException {
        List<Diff> diffs = commitService.getDiffOfCommit(jwt, projectId, sha);
        response.setStatus(diffs == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(diffs);
    }
}
