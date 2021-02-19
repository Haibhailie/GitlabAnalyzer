package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    //Using our VM server for testing purposes
    private final GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca", "FiEWixWRQZJdt2TC_btj");
    private final MergeRequestService mergeRequestService;

    @Autowired
    public MergeRequestController(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;
    }


    //Test using http://localhost:8080/api/core/mergerequests/5/mergeRequests?since=1612508394 on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/mergeRequests")
    public List<MergeRequestDTO> getMergeRequests(@PathVariable int projectId,
                                      @RequestParam (required = false) String since,
                                      @RequestParam (required = false) String until) throws GitLabApiException{

        Date dateSince, dateUntil;
        if(since!=null) {
            dateSince = new Date(Long.parseLong(String.valueOf(since)) * 1000); // since given value
            if(until!=null) {
                dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
        }
        if(until!=null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return MergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);
    }

    //Test using http://localhost:8080/api/core/mergerequests/5/10/commits on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/{mergerequestId}/commits")
    public List<CommitDTO> getCommitsFromMergeRequests(@PathVariable int mergerequestId,
                                                       @PathVariable String projectId) throws GitLabApiException {

        int integerProjectID = Integer.parseInt(projectId);
        return MergeRequestService.getAllCommitsFromMergeRequest(gitLabApi, integerProjectID, mergerequestId);
    }

    //Test using http://localhost:8080/api/core/mergerequests/5/10/diff on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/{mergerequestId}/diff")
    public List<MergeRequestDiffDTO> getDiffsFromMergeRequests(@PathVariable int mergerequestId,
                                                       @PathVariable String projectId) throws GitLabApiException {

        int integerProjectID = Integer.parseInt(projectId);
        return MergeRequestService.getDiffFromMergeRequest(gitLabApi, integerProjectID, mergerequestId);
    }
}
