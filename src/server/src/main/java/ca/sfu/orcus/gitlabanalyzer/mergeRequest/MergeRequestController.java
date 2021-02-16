package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class MergeRequestController {

    private final GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca", "FiEWixWRQZJdt2TC_btj");

    @GetMapping("/api/core/{projectId}/mergeRequests")
    public List<MergeRequestDTO> getMergeRequests(@PathVariable int projectId,
                                      @RequestParam Optional<String> since,
                                      @RequestParam Optional<String> until) throws GitLabApiException{

        Date dateSince, dateUntil;
        if(since.isPresent()) {
            dateSince = new Date(Long.parseLong(String.valueOf(since)) * 1000); // since given value
            if(until.isPresent()) {
                dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
        }
        if(until.isPresent()) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
    }

    @GetMapping("/api/core/{projectId}/{mergerequestId}/commits")
    public List<CommitDTO> getCommitsFromMergeRequests(@PathVariable String mergeRequestId,
                                                       @PathVariable String projectId) throws GitLabApiException {

        int integerMergeRequestID = Integer.parseInt(mergeRequestId);
        int integerProjectID = Integer.parseInt(projectId);
        return MergeRequestService.getAllCommitsFromMergeRequest(gitLabApi, integerProjectID, integerMergeRequestID);

    }

    @GetMapping("/api/core/{projectId}/{mergerequestId}/diff")
    public List<MergeRequestDiffDTO> getDiffsFromMergeRequests(@PathVariable String mergeRequestId,
                                                       @PathVariable String projectId) throws GitLabApiException {

        int integerMergeRequestID = Integer.parseInt(mergeRequestId);
        int integerProjectID = Integer.parseInt(projectId);
        return MergeRequestService.getDiffFromMergeRequest(gitLabApi, integerProjectID, integerMergeRequestID);
    }

}
