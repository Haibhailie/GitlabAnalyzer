package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;

import static ca.sfu.orcus.gitlabanalyzer.commit.CommitService.getAllCommits;
import static ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService.getAllMergeRequests;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MemberService {

    public static List<MemberDTO> getAllMembers(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        List<MemberDTO> allMembers = new ArrayList<>();
        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectID);

        for (Member m : members) {
            MemberDTO presentMember = new MemberDTO(gitLabApi, projectID, m);
            allMembers.add(presentMember);
        }
        return allMembers;
    }

    public static List<CommitDTO> getAllCommitsByMemberName(GitLabApi gitLabApi, int projectID, Date since, Date until, String MemberName) throws GitLabApiException {

        List<CommitDTO> commitsByMemberName = new ArrayList<>();
        ArrayList<CommitDTO> allCommits = getAllCommits(gitLabApi, projectID, since, until);

        for (CommitDTO c : allCommits) {
            if (c.getAuthor().equals(MemberName))
                commitsByMemberName.add(c);
        }
        return commitsByMemberName;
    }

    public static List<MergeRequestDTO> getAllMRsByMemberName(GitLabApi gitLabApi, int projectID, Date since, Date until, String MemberName) throws GitLabApiException {

        List<MergeRequestDTO> mergeRequestsByMemberName = new ArrayList<>();
        List<MergeRequestDTO> allMRs = getAllMergeRequests(gitLabApi,projectID, since, until);
        for (MergeRequestDTO mr : allMRs) {
            if (mr.getAuthor().equals(MemberName))
                mergeRequestsByMemberName.add(mr);
        }
        return mergeRequestsByMemberName;
    }


}
