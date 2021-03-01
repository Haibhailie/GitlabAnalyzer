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

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/project/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId) {
        List<MemberDto> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(members == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(members);

        
    }

    @GetMapping("/api/project/{projectId}/members/{memberEmail}/commits")
    public String getCommitsByMemberEmail(@CookieValue(value = "sessionId") String jwt,
                                          HttpServletResponse response,
                                          @PathVariable int projectId,
                                          @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                          @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                                          @PathVariable String memberEmail) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<CommitDto> allCommitsByMemberEmail = memberService.getCommitsByMemberEmail(jwt, projectId, dateSince, dateUntil, memberEmail);
        response.setStatus(allCommitsByMemberEmail == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(allCommitsByMemberEmail);
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
        List<MergeRequestDto> allMergeRequestsByMemberId = memberService.getMergeRequestsByMemberID(jwt, projectId, dateSince, dateUntil, memberId);
        response.setStatus(allMergeRequestsByMemberId == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(allMergeRequestsByMemberId);
    }
}
