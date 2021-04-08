package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
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
public class MemberController {
    private final MemberService memberService;
    private static final Gson gson = new Gson();

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/project/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId) {
        List<MemberDto> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(members == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(members);
    }

    @GetMapping("/api/project/{projectId}/members/{memberName}/commits")
    public String getCommitsByMemberName(@CookieValue(value = "sessionId") String jwt,
                                          HttpServletResponse response,
                                          @PathVariable int projectId,
                                          @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                          @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                                          @PathVariable String memberName) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<CommitDto> allCommitsByMemberName = memberService.getCommitsByMemberName(jwt, projectId, dateSince, dateUntil, memberName);
        response.setStatus(allCommitsByMemberName == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(allCommitsByMemberName);
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
        List<MergeRequestDto> allMergeRequestsByMemberId = memberService.getMergeRequestsByMemberId(jwt, projectId, dateSince, dateUntil, memberId);
        response.setStatus(allMergeRequestsByMemberId == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(allMergeRequestsByMemberId);
    }

    @GetMapping("/api/project/{projectId}/members/{memberName}/mergerequest")
    public String getOrphanMergeRequestByMemberName(@CookieValue(value = "sessionId") String jwt,
                                         HttpServletResponse response,
                                         @PathVariable int projectId,
                                         @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                         @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                                         @PathVariable String memberName) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<MergeRequestDto> orphanMergeRequestsByMemberName = memberService.getOrphanMergeRequestByMemberName(jwt, projectId, dateSince, dateUntil, memberName);
        response.setStatus(orphanMergeRequestsByMemberName == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(orphanMergeRequestsByMemberName);
    }

    @GetMapping("/api/project/{projectId}/members/{memberName}/{mergerequestId}/commits")
    public String getOrphanCommitsFromOrphanMergeRequestByMemberName(@CookieValue(value = "sessionId") String jwt,
                                                       HttpServletResponse response,
                                                       @PathVariable int projectId,
                                                       @PathVariable int mergerequestId,
                                                       @PathVariable String memberName) {
        List<CommitDto> orphanCommitsForOrphanMergeRequestByMemberName = memberService.getOrphanCommitsFromOrphanMergeRequestByMemberName(jwt, projectId, mergerequestId, memberName);
        response.setStatus(orphanCommitsForOrphanMergeRequestByMemberName == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(orphanCommitsForOrphanMergeRequestByMemberName);
    }
}