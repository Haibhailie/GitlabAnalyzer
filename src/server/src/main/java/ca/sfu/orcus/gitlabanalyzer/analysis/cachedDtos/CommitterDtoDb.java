package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import java.util.HashSet;
import java.util.Set;

public final class CommitterDtoDb {
    private String email;
    private String name;
    private Set<String> commitIds;
    private Set<Integer> mergeRequestIds;
    private MemberDtoDb member;

    public CommitterDtoDb() {}

    public CommitterDtoDb(String email, String name, Set<String> commitIds, Set<Integer> mergeRequestIds, MemberDtoDb member) {
        setEmail(email);
        setName(name);
        setCommitIds(new HashSet<>(commitIds));
        setMergeRequestIds(new HashSet<>(mergeRequestIds));
        setMember(member);
    }

    public CommitterDtoDb setEmail(String email) {
        this.email = email;
        return this;
    }

    public CommitterDtoDb setName(String name) {
        this.name = name;
        return this;
    }

    public CommitterDtoDb setCommitIds(Set<String> commitIds) {
        this.commitIds = commitIds;
        return this;
    }

    public CommitterDtoDb setMergeRequestIds(Set<Integer> mrIds) {
        this.mergeRequestIds = mrIds;
        return this;
    }

    public void setMember(MemberDtoDb member) {
        this.member = member;
    }

    public void addCommitId(String commitId) {
        this.commitIds.add(commitId);
    }

    public void addMergeRequestId(Integer mergeRequestId) {
        this.mergeRequestIds.add(mergeRequestId);
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Set<String> getCommitIds() {
        return commitIds;
    }

    public Set<Integer> getMergeRequestIds() {
        return mergeRequestIds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CommitterDtoDb)) {
            return false;
        }

        CommitterDtoDb c = (CommitterDtoDb) o;

        return (this.email.equals(c.email)
                && this.name.equals(c.name)
                && this.commitIds.equals(c.commitIds)
                && this.mergeRequestIds.equals(c.mergeRequestIds)
                && this.member.equals(c.member));
    }
}
