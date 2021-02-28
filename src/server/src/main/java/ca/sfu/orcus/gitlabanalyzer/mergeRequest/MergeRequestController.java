package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MergeRequestController {
    private final MergeRequestService mergeRequestService;

    @Autowired
    public MergeRequestController(MergeRequestService mergeRequestService) {
        this.mergeRequestService = mergeRequestService;
    }

    @GetMapping("/api/project/{projectId}/mergerequests")
    public String getMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                   HttpServletResponse response,
                                   @PathVariable int projectId,
                                   @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                   @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<MergeRequestDto> mergeRequestDtos = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        response.setStatus(mergeRequestDtos == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(mergeRequestDtos);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commits")
    public String getCommitsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                              HttpServletResponse response,
                                              @PathVariable int mergerequestId,
                                              @PathVariable int projectId) {
        List<CommitDto> commitDTOS = mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(commitDTOS == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(commitDTOS);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/diff")
    public String getDiffsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                            HttpServletResponse response,
                                            @PathVariable int mergerequestId,
                                            @PathVariable int projectId) {
        List<MergeRequestDiffDto> mergeRequestDiffDtos = mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(mergeRequestDiffDtos == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(mergeRequestDiffDtos);
    }
}
