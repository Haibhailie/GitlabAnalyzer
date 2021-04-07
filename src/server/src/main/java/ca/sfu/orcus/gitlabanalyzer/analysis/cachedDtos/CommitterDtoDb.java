package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import java.util.HashSet;
import java.util.Set;

public final class CommitterDtoDb {
    private String email;
    private Set<String> commitIds;
    private Set<Integer> mergeRequestIds;

    public CommitterDtoDb(String email, Set<String> commitIds, Set<Integer> mergeRequestIds) {
        setEmail(email);
        setCommitIds(new HashSet<>(commitIds));
        setMergeRequestIds(new HashSet<>(mergeRequestIds));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCommitIds(Set<String> commitIds) {
        this.commitIds = commitIds;
    }

    public void setMergeRequestIds(Set<Integer> mrIds) {
        this.mergeRequestIds = mrIds;
    }

    public void addCommitId(String commitId) {
        this.commitIds.add(commitId);
    }

    public void addMergeRequestId(Integer mergeRequestId) {
        this.mergeRequestIds.add(mergeRequestId);
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
                && this.commitIds.equals(c.commitIds)
                && this.mergeRequestIds.equals(c.mergeRequestIds));
    }
}
