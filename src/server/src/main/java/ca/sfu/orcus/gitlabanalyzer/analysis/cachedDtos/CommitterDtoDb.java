package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import java.util.List;

public final class CommitterDtoDb {
    private String email;
    private List<String> commitIds;
    private List<Integer> mergeRequestIds;

    public CommitterDtoDb(String email, List<String> commitIds, List<Integer> mergeRequestIds) {
        setEmail(email);
        setCommitIds(commitIds);
        setMergeRequestIds(mergeRequestIds);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCommitIds(List<String> commitIds) {
        this.commitIds = commitIds;
    }

    public void setMergeRequestIds(List<Integer> mrIds) {
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
