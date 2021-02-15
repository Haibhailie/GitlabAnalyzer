package ca.sfu.orcus.gitlabanalyzer.commit;


import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class CommitController {
    private final GitLabApi gitLabApi = null;

    @GetMapping("/api/core/{projectId}/commits")
    public List<CommitDTO> getCommits(@PathVariable int projectId,
                                      @RequestParam Optional<String> since,
                                      @RequestParam Optional<String> until) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        if(since.isPresent()) {
            dateSince = new Date(Long.parseLong(String.valueOf(since)) * 1000); // since given value
            if(until.isPresent()) {
                dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return CommitService.getAllCommits(gitLabApi, projectId, dateSince, dateUntil);
        }
        if(until.isPresent()) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            return CommitService.getAllCommits(gitLabApi, projectId, dateSince, dateUntil);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return CommitService.getAllCommits(gitLabApi, projectId, dateSince, dateUntil);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}")
    public CommitDTO getSingleCommit(@PathVariable int projectId,
                                     @PathVariable String sha) throws GitLabApiException {
        return CommitService.getSingleCommit(gitLabApi, projectId, sha);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}/diff")
    public List<Diff> getSingleCommitDiffs(@PathVariable int projectId,
                                           @PathVariable String sha) throws GitLabApiException {
        return CommitService.getDiffOfCommit(gitLabApi, projectId, sha);
    }



}
