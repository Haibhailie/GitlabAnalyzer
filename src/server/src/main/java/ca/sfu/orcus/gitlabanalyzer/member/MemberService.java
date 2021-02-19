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

    public static List<CommitDTO> getAllCommitsByMemberID(GitLabApi gitLabApi, int projectID, Date since, Date until, int MemberID) throws GitLabApiException {

        List<CommitDTO> commitsByMemberID = new ArrayList<>();
        ArrayList<CommitDTO> allCommits = getAllCommits(gitLabApi, projectID, since, until);

        for (CommitDTO c : allCommits) {
            if (c.getAuthorId() == MemberID)
                commitsByMemberID.add(c);
        }
        return commitsByMemberID;
    }

    public static List<MergeRequestDTO> getAllMRsByMemberID(GitLabApi gitLabApi, int projectID, Date since, Date until, int MemberID) throws GitLabApiException {

        List<MergeRequestDTO> mergeRequestsByMemberID = new ArrayList<>();
        List<MergeRequestDTO> allMRs = getAllMergeRequests(gitLabApi,projectID, since, until);
        for (MergeRequestDTO mr : allMRs) {
            if (mr.getAuthorID == MemberID)
                mergeRequestsByMemberID.add(mr);
        }
        return mergeRequestsByMemberID;
    }


}
