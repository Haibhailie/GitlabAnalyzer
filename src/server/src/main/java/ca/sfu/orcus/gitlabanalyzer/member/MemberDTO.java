package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.models.Member;

import java.util.List;

public class MemberDTO {
    private List<Member> members;


    public void setMembers(List<Member> members) {
        this.members = members;
    }

}
