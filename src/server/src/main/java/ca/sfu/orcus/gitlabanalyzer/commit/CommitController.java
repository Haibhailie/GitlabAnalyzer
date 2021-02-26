package ca.sfu.orcus.gitlabanalyzer.commit;

import com.google.gson.Gson;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.utils.ISO8601;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static ca.sfu.orcus.gitlabanalyzer.Constants.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommitController {
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
        Gson gson = new Gson();
        Date dateSince;
        try {
            dateSince = getDateSince(since);
        } catch (ParseException e) {
            response.setStatus(400);
            return gson.toJson(null);
        }
        Date dateUntil = getDateUntil(until);

        List<CommitDto> commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);

        response.setStatus(commits == null ? 401 : 200);
        return gson.toJson(commits);
    }
    // TODO: ensure that since is earlier than until
    private Date getDateSince(long since) throws ParseException {
        if(since < EARLIEST_DATE_LONG) {
            return ISO8601.toDate(EARLIEST_DATE);  // since 1973
        } else {
            return new Date(since * EPOCH_TO_DATE_FACTOR); // since given value

        }
    }

    private Date getDateUntil(long until) {
        if(until != DEFAULT_UNTIL) {
            return new Date(until * EPOCH_TO_DATE_FACTOR); // until given value
        } else {
            return new Date();                             // until now
        }
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}")
    public String getSingleCommit(@CookieValue(value = "sessionId") String jwt,
                                  @PathVariable int projectId,
                                  @PathVariable String sha,
                                  HttpServletResponse response) {
        CommitDto commit = commitService.getSingleCommit(jwt, projectId, sha);
        response.setStatus(commit == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commit);
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}/diff")
    public String getSingleCommitDiffs(@CookieValue(value = "sessionId") String jwt,
                                       @PathVariable int projectId,
                                       @PathVariable String sha,
                                       HttpServletResponse response) {
        List<Diff> diffs = commitService.getDiffOfCommit(jwt, projectId, sha);
        response.setStatus(diffs == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(diffs);
    }
}
