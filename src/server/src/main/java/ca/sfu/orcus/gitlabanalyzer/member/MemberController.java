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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {
    private final MemberService memberService;
    private final MergeRequestService mergeRequestService;
    private final CommitService commitService;
    int EPOCH_TO_DATE_FACTOR = 1000; //to multiply the long from parseLong() by 1000 to convert to milliseconds, for Java's date constructor


    @Autowired
    public MemberController(MemberService memberService, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberService = memberService;
        this.mergeRequestService = mergeRequestService;
        this.commitService = commitService;

    }

    @GetMapping("/api/core/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response, @PathVariable int projectId) {

        List<MemberDTO> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(members == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(members);
    }

    private Date calculateUntil(long until) {
        if (until == -1) {
            return new Date(); // until now
        } else {
            return new Date(until * EPOCH_TO_DATE_FACTOR); // until given value
        }
    }

    @GetMapping("/api/core/{projectId}/members/{memberEmail}/commits")
    public String getCommitsByMemberEmail(@CookieValue(value = "sessionId") String jwt,
                                          HttpServletResponse response,
                                          @PathVariable int projectId,
                                          @RequestParam(required = false, defaultValue = "0") long since,
                                          @RequestParam(required = false, defaultValue = "-1") long until, @PathVariable String memberEmail) {

        Date dateSince = new Date(since * EPOCH_TO_DATE_FACTOR);
        ;
        Date dateUntil = calculateUntil(until);
        Gson gson = new Gson();
        List<CommitDTO> allCommitsByMemberEmail = new ArrayList<>();
        List<CommitDTO> allCommits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        for (CommitDTO c : allCommits) {
            if (c.getAuthorEmail().equal(memberEmail)) {
                allCommitsByMemberEmail.add(c);
            }
        }
        response.setStatus(allCommitsByMemberEmail == null ? 401 : 200);
        return gson.toJson(allCommitsByMemberEmail);
    }

    @GetMapping("/api/core/{projectId}/members/{memberId}/mergerequests")
    public String getMergeRequestsByMemberID(@CookieValue(value = "sessionId") String jwt,
                                             HttpServletResponse response,
                                             @PathVariable int projectId,
                                             @RequestParam(required = false, defaultValue = "0") long since,
                                             @RequestParam(required = false, defaultValue = "-1") long until, @PathVariable int memberId) throws GitLabApiException {

        Date dateSince = new Date(since * EPOCH_TO_DATE_FACTOR);
        Date dateUntil = calculateUntil(until);
        Gson gson = new Gson();
        List<MergeRequestDTO> allMergeRequestsByMemberId = new ArrayList<>();
        List<MergeRequestDTO> allMergeRequests = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        for (MergeRequestDTO mr : allMergeRequests) {
            if (mr.getUserID() == memberId) {
                allMergeRequestsByMemberId.add(mr);
            }
        }
        response.setStatus(allMergeRequestsByMemberId == null ? 401 : 200);
        return gson.toJson(allMergeRequestsByMemberId);
    }


}
