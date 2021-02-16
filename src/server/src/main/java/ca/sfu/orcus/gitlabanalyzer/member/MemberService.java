package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDTO;
import static ca.sfu.orcus.gitlabanalyzer.commit.CommitService.getAllCommits;
import static ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestRepository.getAllMergeRequests;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MemberService {

    public static ArrayList<MemberDTO> getAllMembers(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MemberDTO> listMember = new ArrayList<>();
        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectID);

        for (Member m : members) {
            MemberDTO presentMember = new MemberDTO(gitLabApi, projectID, m);
            listMember.add(presentMember);
        }
        return listMember;
    }

    public static ArrayList<CommitDTO> getAllCommitsByMemberName(GitLabApi gitLabApi, int projectID, Date since, Date until, String MemberName) throws GitLabApiException {

        ArrayList<CommitDTO> commitsByMemberName = new ArrayList<>();

        ArrayList<CommitDTO> allCommits = getAllCommits(gitLabApi, projectID, since, until);

        for (CommitDTO c : allCommits) {
            if (c.getAuthorName().equals(MemberName))
                commitsByMemberName.add(c);
        }

        return commitsByMemberName;
    }

    public static ArrayList<MergeRequestDTO> getAllMRsByMemberName(GitLabApi gitLabApi, int projectID, String MemberName) throws GitLabApiException {

        ArrayList<MergeRequestDTO> mergeRequestsByMemberName = new ArrayList<>();

        //need static method from MergeRequest
        ArrayList<MergeRequestDTO> allMRs = getAllMergeRequests(gitLabApi, projectID);
        //need getter from MergeRequestDTO
        for (MergeRequestDTO mr : allMRs) {
            if (mr.getAuthorName().equals(MemberName))
                mergeRequestsByMemberName.add(mr);
        }

        return mergeRequestsByMemberName;
    }



}
