package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

public class MemberDto {

    private String displayName;
    private String email;
    private int id;
    private String username;
    private String role;

    public MemberDto(Member presentMember) throws GitLabApiException {
        setDisplayName(presentMember.getName());
        setEmail(presentMember.getEmail());
        setId(presentMember.getId());
        setUsername(presentMember.getUsername());
        setRole(MemberUtils.getMemberRoleFromAccessLevel(presentMember.getAccessLevel().value));
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
