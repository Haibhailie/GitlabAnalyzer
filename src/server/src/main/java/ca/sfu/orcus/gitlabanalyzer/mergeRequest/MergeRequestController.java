package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
public class MergeRequestController {

    private final MergeRequestService mergeRequestService;

    @Autowired
    public MergeRequestController(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;

    }

    @GetMapping("/api/project/{projectId}/mergeRequests")
    public String getMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                  HttpServletResponse response,
                                                  @PathVariable int projectId,
                                                  @RequestParam (required = false) String since,
                                                  @RequestParam (required = false) String until) throws GitLabApiException{

        //to multiply the long from parseLong() by 1000 to convert to milliseconds, for Java's date constructor
        int epochMillisecondScale = 1000;
        Date dateSince, dateUntil;
        Gson gson = new Gson();
        if(since == null)
            dateSince = new Date(0); //since the beginning of time
        else
            dateSince = new Date(Long.parseLong(since) * epochMillisecondScale); // since given value

        if(until == null)
            dateUntil = new Date(); // until now
        else
            dateUntil = new Date(Long.parseLong(until) * epochMillisecondScale); // until given value

        List <MergeRequestDTO> mergeRequestDTOS = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        response.setStatus(mergeRequestDTOS == null ? 401 : 200);

        return gson.toJson(mergeRequestDTOS);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commits")
    public String getCommitsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                       HttpServletResponse response,
                                                       @PathVariable int mergerequestId,
                                                       @PathVariable int projectId) {

        List<CommitDTO> commitDTOS = mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(commitDTOS == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commitDTOS);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/diff")
    public String getDiffsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                                               HttpServletResponse response,
                                                               @PathVariable int mergerequestId,
                                                               @PathVariable int projectId) throws GitLabApiException {

        List<MergeRequestDiffDTO> mergeRequestDiffDTOS=mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(mergeRequestDiffDTOS == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(mergeRequestDiffDTOS);
    }
}
