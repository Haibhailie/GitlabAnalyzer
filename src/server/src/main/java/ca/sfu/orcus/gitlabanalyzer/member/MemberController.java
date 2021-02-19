package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDTO;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final MergeRequestService mergeRequestService;

    @Autowired
    public MemberController(MemberService memberService, MergeRequestService mergeRequestService) {
        this.memberService = memberService;
        this.mergeRequestService = mergeRequestService;

    }

    private final GitLabApi gitLabApi = null; // null because currently unable to verify if gitLabApi is valid

    @GetMapping("/api/core/{projectId}/members")
    public List<MemberDTO> getMembers(@CookieValue(value = "sessionId") String jwt,
                                      HttpServletResponse response,
                                      @PathVariable int projectId,) throws GitLabApiException {
        Gson gson = new Gson();

        List <MemberDTO> memberDTOS = memberService.getAllMembers(jwt, projectId);
        response.setStatus(memberDTOS == null ? 401 : 200);

        return gson.toJson(memberDTOS);
    }

    @GetMapping("/api/core/{projectId}/members/{memberid}/commits")
    public List<CommitDTO> getCommitsByAuthorID(@PathVariable int projectId,
                                                  @RequestParam(required = false) String since,
                                                  @RequestParam(required = false) String until, @PathVariable int memberid) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        if (since != null) {
            dateSince = new Date(Long.parseLong(since) * 1000); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
        }
        if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
    }

    @GetMapping("/api/core/{projectId}/members/{memberid}/mergerequests")
    public List<MergeRequestDTO> getMRsByAuthorID(@PathVariable int projectId,
                                                    @RequestParam(required = false) String since, @RequestParam(required = false) String until,
                                                    @PathVariable int memberid) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        if (since != null) {
            dateSince = new Date(Long.parseLong(since) * 1000); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return MemberService.getAllMRsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
        }
        if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            return MemberService.getAllMRsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return MemberService.getAllMRsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
    }


}
