package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;

/**
 * A simple data transfer object to handle resolving git committers to their respective GitLab members
 */
public class CommitterDto {
    private String email;
    private String name;
    private int id;
    private MemberDto gitLabMember;

    public CommitterDto(String email, String name, int id, MemberDto gitLabMember) {
        setEmail(email);
        setName(name);
        setId(id);
        setGitLabMember(gitLabMember);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGitLabMember(MemberDto gitLabMember) {
        this.gitLabMember = gitLabMember;
    }
}
