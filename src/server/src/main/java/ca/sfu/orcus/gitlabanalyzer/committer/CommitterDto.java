package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;

/**
 * A simple data transfer object to handle resolving git committers to their respective GitLab members.
 */
public class CommitterDto {
    private String email;
    private String name;
    private MemberDto member;

    public CommitterDto(String email, String name, MemberDto member) {
        setEmail(email);
        setName(name);
        setMember(member);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMember(MemberDto member) {
        this.member = member;
    }
}
