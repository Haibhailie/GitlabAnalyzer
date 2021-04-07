package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {
    private final MemberService memberService;
    private static final Gson gson = new Gson();

    @Autowired
    public MemberController(@Qualifier("direct") MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/project/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId) {
        List<MemberDto> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(getResponseCode(members));
        return gson.toJson(members);
    }

    private int getResponseCode(List<MemberDto> list) {
        if (list == null) {
            return SC_UNAUTHORIZED;
        } else if (list.isEmpty()) {
            return SC_NOT_FOUND;
        } else {
            return SC_OK;
        }
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
}
