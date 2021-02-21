package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.gitlab4j.api.utils.ISO8601;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {
    private final MemberService memberService;
    private final MergeRequestService mergeRequestService;
    private final CommitService commitService;

    int EPOCH_TO_DATE_FACTOR = 1000; //to multiply the long from parseLong() by 1000 to convert to milliseconds, for Java's date constructor
    private static final String EARLIEST_DATE = "1973-03-30T00:00:00Z"; // earliest date commitsApi works with
    private static final long EARLIEST_DATE_LONG = 102297600;
    private static final long DEFAULT_UNTIL = -1;

    @Autowired
    public MemberController(MemberService memberService, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberService = memberService;
        this.mergeRequestService = mergeRequestService;
        this.commitService = commitService;
    }

    @GetMapping("/api/project/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response, @PathVariable int projectId) {
        List<MemberDto> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(members == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(members);
    }

    @GetMapping("/api/project/{projectId}/members/{memberEmail}/commits")
    public String getCommitsByMemberEmail(@CookieValue(value = "sessionId") String jwt,
                                          HttpServletResponse response,
                                          @PathVariable int projectId,
                                          @RequestParam(required = false, defaultValue = "0") long since,
                                          @RequestParam(required = false, defaultValue = "-1") long until, @PathVariable String memberEmail) {
        Date dateSince;
        Gson gson = new Gson();
        try {
            dateSince = getDateSince(since);
        } catch (ParseException e) {
            response.setStatus(400);
            return gson.toJson(null);
        }
        Date dateUntil = getDateUntil(until);

        List<CommitDto> allCommits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        List<CommitDto> allCommitsByMemberEmail = memberService.getCommitsByMemberEmail(allCommits,memberEmail);
        response.setStatus(allCommitsByMemberEmail == null ? 401 : 200);
        return gson.toJson(allCommitsByMemberEmail);
    }

    private Date getDateSince(long since) throws ParseException {
        if (since < EARLIEST_DATE_LONG) {
            return ISO8601.toDate(EARLIEST_DATE);  // since 1973
        } else {
            return new Date(since * EPOCH_TO_DATE_FACTOR); // since given value
        }
    }

    private Date getDateUntil(long until) {
        if (until != DEFAULT_UNTIL) {
            return new Date(until * EPOCH_TO_DATE_FACTOR); // until given value
        } else {
            return new Date();                             // until now
        }
    }

    @GetMapping("/api/project/{projectId}/members/{memberId}/mergerequests")
    public String getMergeRequestsByMemberID(@CookieValue(value = "sessionId") String jwt,
                                             HttpServletResponse response,
                                             @PathVariable int projectId,
                                             @RequestParam(required = false, defaultValue = "0") long since,
                                             @RequestParam(required = false, defaultValue = "-1") long until, @PathVariable int memberId) {
        Date dateSince;
        Gson gson = new Gson();
        try {
            dateSince = getDateSince(since);
        } catch (ParseException e) {
            response.setStatus(400);
            return gson.toJson(null);
        }
        Date dateUntil = getDateUntil(until);
        List<MergeRequestDto> allMergeRequests = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        List<MergeRequestDto> allMergeRequestsByMemberId = memberService.getMergeRequestsByMemberID(allMergeRequests,memberId);
        response.setStatus(allMergeRequestsByMemberId == null ? 401 : 200);
        return gson.toJson(allMergeRequestsByMemberId);
    }
}
