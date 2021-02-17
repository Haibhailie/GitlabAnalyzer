package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDTO;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class MemberController {

    private final GitLabApi gitLabApi = null; // null because currently unable to verify if gitLabApi is valid

    @GetMapping("/api/core/{projectId}/members")
    public List<MemberDTO> getMembers(@PathVariable int projectId) throws GitLabApiException {

        return MemberService.getAllMembers(gitLabApi, projectId);
    }

    @GetMapping("/api/core/{projectId}/members/{authorname}/commits")
    public List<CommitDTO> getCommitsByAuthorName(@PathVariable int projectId,
                                                  @RequestParam(required = false) String since,
                                                  @RequestParam(required = false) String until, @PathVariable String authorname) throws GitLabApiException {

        Date dateSince;
        Date dateUntil;
        if (since != null) {
            dateSince = new Date(Long.parseLong(since) * 1000); // since given value
            if (until != null) {
                dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            } else {
                dateUntil = new Date(); // until now
            }
            return MemberService.getAllCommitsByMemberName(gitLabApi, projectId, dateSince, dateUntil, authorname);
        }
        if (until != null) {
            dateSince = new Date(0); // since 1969
            dateUntil = new Date(Long.parseLong(until) * 1000); // until given value
            return MemberService.getAllCommitsByMemberName(gitLabApi, projectId, dateSince, dateUntil, authorname);
        }
        dateSince = new Date(0); // since 1969
        dateUntil = new Date(); // until now
        return MemberService.getAllCommitsByMemberName(gitLabApi, projectId, dateSince, dateUntil, authorname);
    }


}
