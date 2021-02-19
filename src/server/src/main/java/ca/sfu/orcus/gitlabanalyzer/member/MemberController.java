package ca.sfu.orcus.gitlabanalyzer.member;

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

    @Autowired
    public MemberController(MemberService memberService, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberService = memberService;
        this.mergeRequestService = mergeRequestService;

    }

    @GetMapping("/api/core/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response, @PathVariable int projectId) throws GitLabApiException {
        Gson gson = new Gson();
        List<MemberDTO> memberDTOS = memberService.getAllMembers(jwt, projectId);
        response.setStatus(memberDTOS == null ? 401 : 200);
        return gson.toJson(memberDTOS);
    }
//
//    @GetMapping("/api/core/{projectId}/members/{memberid}/commits")
//    public List<CommitDTO> getCommitsByAuthorID(@PathVariable int projectId,
//                                                  @RequestParam(required = false) String since,
//                                                  @RequestParam(required = false) String until, @PathVariable int memberid) throws GitLabApiException {
//
//        Date dateSince;
//        Date dateUntil;
//        if (since != null) {
//            dateSince = new Date(Long.parseLong(since) * 1000); // since given value
//            if (until != null) {
//                dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
//            } else {
//                dateUntil = new Date(); // until now
//            }
//            return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
//        }
//        if (until != null) {
//            dateSince = new Date(0); // since 1969
//            dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
//            return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
//        }
//        dateSince = new Date(0); // since 1969
//        dateUntil = new Date(); // until now
//        return MemberService.getAllCommitsByMemberID(gitLabApi, projectId, dateSince, dateUntil, memberid);
//    }
//
    @GetMapping("/api/core/{projectId}/members/{memberId}/mergerequests")
    public String getMRsByAuthorID(@CookieValue(value = "sessionId") String jwt,
                                   HttpServletResponse response, @PathVariable int projectId,
                                   @RequestParam(required = false) String since, @RequestParam(required = false) String until,
                                   @PathVariable int memberId) throws GitLabApiException {
        Gson gson = new Gson();
        Date dateSince,dateUntil;
        List<MergeRequestDTO> mergeRequestsByMemberID = new ArrayList<>();
        if (since != null) {
            dateSince = new Date(Long.parseLong(String.valueOf(since)) * 1000); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }

            List<MergeRequestDTO> allmergeRequests = mergeRequestService.getAllMergeRequests(jwt, projectId,dateSince,dateUntil);
            for (MergeRequestDTO mr : allmergeRequests) {
            if (mr.getUserID() == memberId)
                mergeRequestsByMemberID.add(mr);
        }
            response.setStatus(mergeRequestsByMemberID == null ? 401 : 200);
            return gson.toJson(mergeRequestsByMemberID);
        }
        if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(String.valueOf(until)) * 1000); // until given value
            List<MergeRequestDTO> allmergeRequests = mergeRequestService.getAllMergeRequests(jwt, projectId,dateSince,dateUntil);
            for (MergeRequestDTO mr : allmergeRequests) {
                if (mr.getUserID() == memberId)
                    mergeRequestsByMemberID.add(mr);
            }
            response.setStatus(mergeRequestsByMemberID == null ? 401 : 200);
            return gson.toJson(mergeRequestsByMemberID);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        List<MergeRequestDTO> allmergeRequests = mergeRequestService.getAllMergeRequests(jwt, projectId,dateSince,dateUntil);
        for (MergeRequestDTO mr : allmergeRequests) {
            if (mr.getUserID() == memberId)
                mergeRequestsByMemberID.add(mr);
        }
        response.setStatus(mergeRequestsByMemberID == null ? 401 : 200);
        return gson.toJson(mergeRequestsByMemberID);
    }


}
