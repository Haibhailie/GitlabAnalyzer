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

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MergeRequestController {
    private final MergeRequestService mergeRequestService;
    private static final Gson gson = new Gson();

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
        response.setStatus(mergeRequestDtos == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(mergeRequestDtos);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commits")
    public String getCommitsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                              HttpServletResponse response,
                                              @PathVariable int projectId,
                                              @PathVariable int mergerequestId) {
        List<CommitDto> commitDtos = mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(commitDtos == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(commitDtos);
    }

    @GetMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/diff")
    public String getDiffsFromMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                            HttpServletResponse response,
                                            @PathVariable int projectId,
                                            @PathVariable int mergerequestId) {
        String mergeRequestDiff = mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergerequestId);
        response.setStatus(mergeRequestDiff == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(mergeRequestDiff);
    }

    @GetMapping("/api/project/{projectId}/members/{memberId}/mergerequests")
    public String getMergeRequestsByMemberID(@CookieValue(value = "sessionId") String jwt,
                                             HttpServletResponse response,
                                             @PathVariable int projectId,
                                             @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                             @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                                             @PathVariable int memberId) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<MergeRequestDto> allMergeRequestsByMemberId = mergeRequestService.getMergeRequestsByMemberId(jwt, projectId, dateSince, dateUntil, memberId);
        response.setStatus(allMergeRequestsByMemberId == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(allMergeRequestsByMemberId);
    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/ignore/true")
    public void ignoreMergeRequest(@CookieValue(value = "sessionId") String jwt,
                                   HttpServletResponse response,
                                   @PathVariable int projectId,
                                   @PathVariable int mergerequestId) {

    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/ignore/false")
    public void unignoreMergeRequest(@CookieValue(value = "sessionId") String jwt,
                                   HttpServletResponse response,
                                   @PathVariable int projectId,
                                   @PathVariable int mergerequestId) {

    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/file/{fileId}/ignore/true")
    public void ignoreFile(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId,
                             @PathVariable int mergerequestId,
                             @PathVariable String fileId) {

    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/file/{fileId}/ignore/false")
    public void unignoreFile(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId,
                             @PathVariable int mergerequestId,
                             @PathVariable String fileId) {

    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commit/{commitId}/ignore/true")
    public void ignoreCommit(@CookieValue(value = "sessionId") String jwt,
                           HttpServletResponse response,
                           @PathVariable int projectId,
                           @PathVariable int mergerequestId,
                           @PathVariable String commitId) {

    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commit/{commitId}/ignore/false")
    public void unignoreCommit(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId,
                             @PathVariable int mergerequestId,
                             @PathVariable String commitId) {

    }

}
