package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class MergeRequestController {

    //Using our VM server for testing purposes
    //private final GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca", "FiEWixWRQZJdt2TC_btj");
    private final MergeRequestService mergeRequestService;

    @Autowired
    public MergeRequestController(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;
        //this.commitService = commitService;
    }


    //Test using http://localhost:8080/api/core/mergerequests/5/mergeRequests?since=1612508394 on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/mergeRequests")
    public String getMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                  HttpServletResponse response,
                                                  @PathVariable int projectId,
                                                  @RequestParam (required = false) String since,
                                                  @RequestParam (required = false) String until) throws GitLabApiException{

        Gson gson = new Gson();
        Date dateSince, dateUntil;
        if(since!=null) {
            dateSince = new Date(Long.parseLong(String.valueOf(since)) * 1000); // since given value
            if(until!=null) {
                dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }

            List <MergeRequestDTO> mergeRequestDTOS = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
            response.setStatus(mergeRequestDTOS == null ? 401 : 200);
            return gson.toJson(mergeRequestDTOS);
        }
        if(until!=null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value

            List <MergeRequestDTO> mergeRequestDTOS = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
            response.setStatus(mergeRequestDTOS == null ? 401 : 200);
            return gson.toJson(mergeRequestDTOS);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now

        List <MergeRequestDTO> mergeRequestDTOS = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        response.setStatus(mergeRequestDTOS == null ? 401 : 200);
        return gson.toJson(mergeRequestDTOS);
    }

    //Test using http://localhost:8080/api/core/mergerequests/5/10/commits on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/{mergerequestId}/commits")
    public String getCommitsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                       HttpServletResponse response,
                                                       @PathVariable int mergerequestId,
                                                       @PathVariable String projectId) throws GitLabApiException {
        Gson gson = new Gson();
        int integerProjectID = Integer.parseInt(projectId);
        List<CommitDTO> commitDTOS = mergeRequestService.getAllCommitsFromMergeRequest(jwt, integerProjectID, mergerequestId);
        response.setStatus(commitDTOS == null ? 401 : 200);
        return gson.toJson(commitDTOS);
    }

    //Test using http://localhost:8080/api/core/mergerequests/5/10/diff on Postman
    @GetMapping("/api/core/mergerequests/{projectId}/{mergerequestId}/diff")
    public String getDiffsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                               HttpServletResponse response,
                                                               @PathVariable int mergerequestId,
                                                               @PathVariable String projectId) throws GitLabApiException {
        Gson gson = new Gson();
        int integerProjectID = Integer.parseInt(projectId);
        List<MergeRequestDiffDTO> mergeRequestDiffDTOS=mergeRequestService.getDiffFromMergeRequest(jwt, integerProjectID, mergerequestId);
        response.setStatus(mergeRequestDiffDTOS == null ? 401 : 200);
        return gson.toJson(mergeRequestDiffDTOS);
    }
}
