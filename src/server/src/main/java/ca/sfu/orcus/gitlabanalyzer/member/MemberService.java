package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.sfu.orcus.gitlabanalyzer.commit.CommitService.getAllCommits;

public class MemberService {
    private final static String defaultBranch = "master";

    public ArrayList<MemberDTO> getAllMembers(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MemberDTO> listMember = new ArrayList<>();
        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectID);

        for (Member m : members) {
            MemberDTO presentMember = new MemberDTO(gitLabApi, projectID, m);
            listMember.add(presentMember);
        }
        return listMember;
    }

    public ArrayList<CommitDTO> getAllCommitsByMemberName(GitLabApi gitLabApi, int projectID, Date since, Date until, String MemberName) throws GitLabApiException {

        ArrayList<CommitDTO> listCommit = new ArrayList<>();

        ArrayList<CommitDTO> allCommits = getAllCommits(gitLabApi, projectID, since, until);

        for (CommitDTO c : allCommits) {
            if (c.getAuthor().equals(MemberName))
                listCommit.add(c);
        }

        return listCommit;
    }
}
