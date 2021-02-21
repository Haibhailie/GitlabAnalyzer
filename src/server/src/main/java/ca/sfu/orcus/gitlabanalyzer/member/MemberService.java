package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationService authService;

    @Autowired
    public MemberService(MemberRepository memberRepository, AuthenticationService authService) {
        this.memberRepository = memberRepository;
        this.authService = authService;
    }

    public List<MemberDto> getAllMembers(String jwt, int projectID) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        try {
            List<MemberDto> filteredAllMembers = new ArrayList<>();
            List<Member> allMembers = gitLabApi.getProjectApi().getAllMembers(projectID);
            for (Member m : allMembers) {
                MemberDto presentMember = new MemberDto(m);
                filteredAllMembers.add(presentMember);
            }
            return filteredAllMembers;
        } catch (GitLabApiException g) {
            return null;
        }
    }

}
