package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

public class MemberDto {

    private String displayName;
    private String email;
    private Integer id;
    private String username;
    private String member_role;

    public MemberDto(Member presentMember) throws GitLabApiException {
        setDisplayName(presentMember.getName());
        setEmail(presentMember.getEmail());
        setId(presentMember.getId());
        setUsername(presentMember.getUsername());
        setMemberRole(MemberUtils.getMemberRoleFromAccessLevel(presentMember.getAccessLevel().value));
    }


    public void setDisplayName(String name) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMemberRole(String member_role) {
        this.member_role = member_role;
    }

}
