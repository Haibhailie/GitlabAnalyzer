package ca.sfu.orcus.gitlabanalyzer.member;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.List;


public class MemberRepository {
    public ArrayList<MemberDTO> getAllMembers(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MemberDTO> listMember = new ArrayList<>();
        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectID);
        String presentProjectName = gitLabApi.getProjectApi().getProject(projectID).getName();

        System.out.println("\n\nThe present members in " + presentProjectName + " are:");

        for (Member m : members) {

            MemberDTO presentMember = new MemberDTO();

            presentMember.setName(m.getName());

            presentMember.setEmail(m.getEmail());

            presentMember.setId(m.getId());

            presentMember.setState(m.getState());

            presentMember.setUsername(m.getUsername());

            listMember.add(presentMember);
        }
        return listMember;
    }

}
