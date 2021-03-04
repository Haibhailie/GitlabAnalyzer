package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;

import java.util.Locale;

public class MemberDto {
    private String displayName;
    private String email;
    private int id;
    private String username;
    private String role;

    public MemberDto(Member presentMember) {
        setDisplayName(presentMember.getName());
        setEmail(presentMember.getEmail());
        setId(presentMember.getId());
        setUsername(presentMember.getUsername());
        try {
            setRole(MemberUtils.getMemberRoleFromAccessLevel(presentMember.getAccessLevel().value));
        } catch (NullPointerException e) {
            setRole("GUEST");
        }
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MemberDto)) {
            return false;
        }

        MemberDto c = (MemberDto) o;

        return (this.displayName.equals(c.displayName)
                && this.email.equals(c.email)
                && this.id == c.id
                && this.username.equals(c.username)
                && (this.role).equals(c.role));
    }

}
