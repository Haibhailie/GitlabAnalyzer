package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static ca.sfu.orcus.gitlabanalyzer.Constants.*;

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
                                   @RequestParam(required = false, defaultValue = "0") long since,
                                   @RequestParam(required = false, defaultValue = "-1") long until) {

        Date dateSince = new Date(since * EPOCH_TO_DATE_FACTOR);
        Date dateUntil = calculateUntil(until);
        Gson gson = new Gson();
        List<MergeRequestDto> mergeRequestDtos = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        response.setStatus(mergeRequestDtos == null ? 401 : 200);
        return gson.toJson(mergeRequestDtos);
    }

    private Date calculateUntil(long until) {
        if (until == -1) {
            return new Date(); // until now
        }
        else {
            return new Date(until * EPOCH_TO_DATE_FACTOR); // until given value
        }
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
