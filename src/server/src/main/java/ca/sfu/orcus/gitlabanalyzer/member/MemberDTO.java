package ca.sfu.orcus.gitlabanalyzer.member;

//Vaild access levels
//No access (0)
//Minimal access (5) (Introduced in GitLab 13.5.)
//Guest (10)
//Reporter (20)
//Developer (30)
//Maintainer (40)
//Owner (50)

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Date;
import java.util.List;

public class MemberDTO {

    private String name;
    private String email;
    private Integer id;
    private String username;
    private String state;
    private Integer access_level;
    private String avatar_url;
    private Date expires_at;

    public MemberDTO(GitLabApi gitLabApi, int projectID, Member presentMember) throws GitLabApiException {
        setName(presentMember.getName());
        setEmail(presentMember.getEmail());
        setId(presentMember.getId());
        setUsername(presentMember.getUsername());
        setState(presentMember.getState());
        setAccess_level(presentMember.getAccessLevel().value);
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

    public void setAccess_level(Integer access_level) {
        this.access_level = access_level;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setExpires_at(Date expires_at) {
        this.expires_at = expires_at;
    }


}
