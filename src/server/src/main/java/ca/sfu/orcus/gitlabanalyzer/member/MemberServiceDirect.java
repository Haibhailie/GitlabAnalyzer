package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberServiceDirect {
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public MemberServiceDirect(GitLabApiWrapper gitLabApiWrapper) {
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<MemberDto> getAllMembers(String jwt, int projectId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        return getAllMembers(gitLabApi, projectId);
    }

    private List<MemberDto> getAllMembers(GitLabApi gitLabApi, int projectId) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            return getFilteredMembers(gitLabApi, projectId);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private List<MemberDto> getFilteredMembers(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        List<MemberDto> filteredAllMembers = new ArrayList<>();
        List<Member> allMembers = gitLabApi.getProjectApi().getAllMembers(projectId);
        for (Member m : allMembers) {
            MemberDto presentMember = new MemberDto(m);
            filteredAllMembers.add(presentMember);
        }
        return filteredAllMembers;
    }
}
