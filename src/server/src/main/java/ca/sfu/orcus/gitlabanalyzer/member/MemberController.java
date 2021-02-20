package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final MergeRequestService mergeRequestService;
    private static final long EPOCH_TO_DATE_FACTOR = 1000;
    private final CommitService commitService;

    @Autowired
    public MemberController(MemberService memberService, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberService = memberService;
        this.mergeRequestService = mergeRequestService;
        this.commitService = commitService;

    }

    @GetMapping("/api/core/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response, @PathVariable int projectId) throws GitLabApiException {

        List<MemberDTO> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(members == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(members);
    }

    @GetMapping("/api/core/{projectId}/members/{memberId}/commits")
    public String getCommitsByAuthorID(@CookieValue(value = "sessionId") String jwt,
                                       HttpServletResponse response,
                                       @PathVariable int projectId,
                                       @RequestParam(required = false) String since,
                                       @RequestParam(required = false) String until, @PathVariable int memberId) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        List<CommitDTO> allCommitsByAuthorId;
        if (since != null) {
            dateSince = new Date(Long.parseLong(since) * EPOCH_TO_DATE_FACTOR); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(until) * EPOCH_TO_DATE_FACTOR); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            allCommitsByAuthorId = commitService.getAllCommits(jwt,projectId,dateSince,dateUntil);
        }else if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            allCommitsByAuthorId = commitService.getAllCommits(jwt,projectId,dateSince,dateUntil);
        }else {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(); // until now
            allCommitsByAuthorId = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        }
        response.setStatus(allCommitsByAuthorId == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(allCommitsByAuthorId);
    }

    @GetMapping("/api/core/{projectId}/members/{memberId}/mergerequests")
    public String getMergeRequestsByAuthorID(@CookieValue(value = "sessionId") String jwt,
                                       HttpServletResponse response,
                                       @PathVariable int projectId,
                                       @RequestParam(required = false) String since,
                                       @RequestParam(required = false) String until, @PathVariable int memberId) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        List<MergeRequestDTO> allMergeRequestsByAuthorId;
        if (since != null) {
            dateSince = new Date(Long.parseLong(since) * EPOCH_TO_DATE_FACTOR); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(until) * EPOCH_TO_DATE_FACTOR); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            allMergeRequestsByAuthorId = mergeRequestService.getAllMergeRequests(jwt,projectId,dateSince,dateUntil);
        }else if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(until) * EPOCH_TO_DATE_FACTOR); // until given value
            allMergeRequestsByAuthorId = mergeRequestService.getAllMergeRequests(jwt,projectId,dateSince,dateUntil);
        }else {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(); // until now
            allMergeRequestsByAuthorId = mergeRequestService.getAllMergeRequests(jwt,projectId,dateSince,dateUntil);
        }
        response.setStatus(allMergeRequestsByAuthorId == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(allMergeRequestsByAuthorId);
    }


}
