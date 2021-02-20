package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

import java.util.Date;

public class MemberDto {

    private String name;
    private String email;
    private Integer id;
    private String username;
    private String state;
    private String member_role;
    private String avatar_url;
    private Date expires_at;

    public MemberDto(Member presentMember) throws GitLabApiException {
        setName(presentMember.getName());
        setEmail(presentMember.getEmail());
        setId(presentMember.getId());
        setUsername(presentMember.getUsername());
        setState(presentMember.getState());
        setMemberRole(MemberUtils.getMemberRoleFromAccessLevel(presentMember.getAccessLevel().value));
        setAvatar_url(presentMember.getAvatarUrl());
        setExpires_at(presentMember.getExpiresAt());
    }


    public void setName(String name) {
        this.name = name;
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

    public void setState(String state) {
        this.state = state;
    }

    public void setMemberRole(String member_role) {
        this.member_role = member_role;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setExpires_at(Date expires_at) {
        this.expires_at = expires_at;
    }

}
