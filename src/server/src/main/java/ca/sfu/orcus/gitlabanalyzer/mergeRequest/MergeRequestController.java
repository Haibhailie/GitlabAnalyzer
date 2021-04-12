package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MergeRequestController {
    private final MergeRequestService mergeRequestService;
    private final MergeRequestRepository mergeRequestRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private static final Gson gson = new Gson();

    @Autowired
    public MergeRequestController(MergeRequestService mergeRequestService, MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.mergeRequestService = mergeRequestService;
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    @GetMapping("/api/project/{projectId}/mergerequests")
    public String getMergeRequests(@CookieValue(value = "sessionId") String jwt,
                                   HttpServletResponse response,
                                   @PathVariable int projectId) {
        List<MergeRequestDtoDb> mergeRequestDtos = mergeRequestService.getAllMergeRequests(jwt, projectId);
        response.setStatus(mergeRequestDtos == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(mergeRequestDtos);
    }

//    @GetMapping("/api/project/{projectId}/members/{memberId}/mergerequests")
//    public String getMergeRequestsByMemberID(@CookieValue(value = "sessionId") String jwt,
//                                             HttpServletResponse response,
//                                             @PathVariable int projectId,
//                                             @PathVariable int memberId) {
//        List<MergeRequestDto> allMergeRequestsByMemberId = mergeRequestService.getMergeRequestsByMemberId(jwt, projectId, dateSince, dateUntil, memberId);
//        response.setStatus(allMergeRequestsByMemberId == null ? SC_UNAUTHORIZED : SC_OK);
//        return gson.toJson(allMergeRequestsByMemberId);
//    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/ignore/{doIgnore}")
    public void updateMergeRequestIgnore(@CookieValue(value = "sessionId") String jwt,
                                         HttpServletResponse response,
                                         @PathVariable int projectId,
                                         @PathVariable int mergerequestId,
                                         @PathVariable boolean doIgnore) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent()) {
            mergeRequestService.updateMergeRequestIgnore(projectUrl.get(), mergerequestId, doIgnore);
            response.setStatus(SC_OK);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/file/{fileId}/ignore/{doIgnore}")
    public void ignoreMergeRequestFile(@CookieValue(value = "sessionId") String jwt,
                                       HttpServletResponse response,
                                       @PathVariable int projectId,
                                       @PathVariable int mergerequestId,
                                       @PathVariable String fileId,
                                       @PathVariable boolean doIgnore) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent()) {
            mergeRequestService.updateMergeRequestFileIgnore(projectUrl.get(), mergerequestId, fileId, doIgnore);
            response.setStatus(SC_OK);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commit/{commitId}/ignore/{doIgnore}")
    public void ignoreCommit(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId,
                             @PathVariable int mergerequestId,
                             @PathVariable String commitId,
                             @PathVariable boolean doIgnore) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent()) {
            mergeRequestService.updateCommitIgnore(projectUrl.get(), mergerequestId, commitId, doIgnore);
            response.setStatus(SC_OK);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    @Deprecated
    @PutMapping("/api/project/{projectId}/mergerequest/{mergerequestId}/commit/{commitId}/file/{fileId}/ignore/{doIgnore}")
    public void ignoreCommitFile(@CookieValue(value = "sessionId") String jwt,
                                 HttpServletResponse response,
                                 @PathVariable int projectId,
                                 @PathVariable int mergerequestId,
                                 @PathVariable String commitId,
                                 @PathVariable String fileId,
                                 @PathVariable boolean doIgnore) {
        // TODO: Allow the user to toggle isIgnored for a commit file
        response.setStatus(SC_NOT_IMPLEMENTED);
    }

}