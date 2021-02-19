package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;


import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestRepository;
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

    @Autowired
    public MemberService(MemberRepository memberRepository, AuthenticationService authService) {
        this.memberRepository = memberRepository;
        this.authService = authService;
    }

    public List<MemberDTO> getAllMembers(String jwt, int projectID) throws GitLabApiException {

        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
        List<MemberDTO> allMembers = new ArrayList<>();
        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectID);

        for (Member m : members) {
            MemberDTO presentMember = new MemberDTO(gitLabApi, projectID, m);
            allMembers.add(presentMember);
        }
        return allMembers;
        } else {
            return null;
        }
    }

    public List<CommitDTO> getAllCommitsByMemberID(GitLabApi gitLabApi, int projectID, Date since, Date until, int MemberID) throws GitLabApiException {

        List<CommitDTO> commitsByMemberID = new ArrayList<>();
        ArrayList<CommitDTO> allCommits = getAllCommits(gitLabApi, projectID, since, until);

        for (CommitDTO c : allCommits) {
            if (c.getAuthorId() == MemberID)
                commitsByMemberID.add(c);
        }
        return commitsByMemberID;
    }

    public List<MergeRequestDTO> getAllMRsByMemberID(GitLabApi gitLabApi, int projectID, Date since, Date until, int MemberID) throws GitLabApiException {

        List<MergeRequestDTO> mergeRequestsByMemberID = new ArrayList<>();
        List<MergeRequestDTO> allMRs = getAllMergeRequests(gitLabApi,projectID, since, until);
        for (MergeRequestDTO mr : allMRs) {
            if (mr.getUserID() == MemberID)
                mergeRequestsByMemberID.add(mr);
        }
        return mergeRequestsByMemberID;
    }


}
