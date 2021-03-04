package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
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
    private final GitLabApiWrapper gitLabApiWrapper;
    private final MergeRequestService mergeRequestService;
    private final CommitService commitService;

    @Autowired
    public MemberService(MemberRepository memberRepository, GitLabApiWrapper gitLabApiWrapper, MergeRequestService mergeRequestService, CommitService commitService) {
        this.memberRepository = memberRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.mergeRequestService = mergeRequestService;
        this.commitService = commitService;
    }

    public List<MemberDto> getAllMembers(String jwt, int projectID) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getAllMembers(gitLabApi, projectID);
        } else {
            return null;
        }
    }

    public List<MemberDto> getAllMembers(GitLabApi gitLabApi, int projectId) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            List<MemberDto> filteredAllMembers = new ArrayList<>();
            List<Member> allMembers = gitLabApi.getProjectApi().getAllMembers(projectId);
            for (Member m : allMembers) {
                MemberDto presentMember = new MemberDto(m);
                filteredAllMembers.add(presentMember);
            }
            return filteredAllMembers;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public List<CommitDto> getCommitsByMemberEmail(String jwt, int projectId, Date since, Date until, String memberEmail) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return commitService.getAllCommitDtos(gitLabApi, projectId, since, until, memberEmail);
    }

    public List<MergeRequestDto> getMergeRequestsByMemberID(String jwt, int projectId, Date since, Date until, int memberId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return mergeRequestService.getAllMergeRequests(gitLabApi, projectId, since, until, memberId);
    }
}
