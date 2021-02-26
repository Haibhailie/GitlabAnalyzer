package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationService authService;
    private final MergeRequestService mergeRequestService;
    private final CommitService commitService;

    @Autowired
    public MemberService(MemberRepository memberRepository, AuthenticationService authService, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberRepository = memberRepository;
        this.authService = authService;
        this.mergeRequestService = mergeRequestService;
        this.commitService = commitService;
    }

    public List<MemberDto> getAllMembers(String jwt, int projectID) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getAllMembers(gitLabApi, projectID);
        } else {
            return null;
        }
    }

    public List<MemberDto> getAllMembers(GitLabApi gitLabApi, int projectId) {
        try {
            List<MemberDto> filteredAllMembers = new ArrayList<>();
            List<Member> allMembers = gitLabApi.getProjectApi().getAllMembers(projectId);
            for (Member m : allMembers) {
                MemberDto presentMember = new MemberDto(m);
                filteredAllMembers.add(presentMember);
            }
            return filteredAllMembers;
        } catch (GitLabApiException | NullPointerException e) {
            return null;
        }
    }

    public List<CommitDto> getCommitsByMemberEmail(String jwt, int projectId, Date since, Date until, String memberEmail) {
        List<CommitDto> allCommits = commitService.getAllCommits(jwt, projectId, since, until);
        List<CommitDto> allCommitsByMemberEmail = new ArrayList<>();
        for (CommitDto c : allCommits) {
            if (c.getAuthorEmail().equals(memberEmail)) {
                allCommitsByMemberEmail.add(c);
            }
        }
        return allCommitsByMemberEmail;
    }

    public List<MergeRequestDto> getMergeRequestsByMemberID(String jwt, int projectId, Date since, Date until, int memberId) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        return mergeRequestService.getAllMergeRequests(gitLabApi, projectId, since, until, memberId);
    }
}
