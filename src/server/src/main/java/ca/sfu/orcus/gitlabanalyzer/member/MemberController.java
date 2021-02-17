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

    }
