package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommitDTO {
    private String title;
    private String author;
    private String authorEmail;
    private String id;
    private Date dateCommitted;
    private String message;
    private int numAdditions;
    private int numDeletions;
    private int total;
    private List<Diff> diffs;

    public CommitDTO(GitLabApi gitLabApi, int projectID, Commit commit) throws GitLabApiException {
        this.setTitle(commit.getTitle());
        this.setAuthor(commit.getAuthorName());
        this.setAuthorEmail(commit.getAuthorEmail());
        this.setId(commit.getId());
        this.setDateCommitted(commit.getCommittedDate());
        this.setMessage(commit.getMessage());

        Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectID, commit.getShortId()); // Needed otherwise getStats() returns null
        this.setNumAdditions(presentCommit.getStats().getAdditions());
        this.setNumDeletions(presentCommit.getStats().getDeletions());
        this.setTotal(presentCommit.getStats().getTotal());

        List<Diff> allDiffs = new ArrayList<>();
        List<Diff> gitDiffs = gitLabApi.getCommitsApi().getDiff(projectID, commit.getId());
        allDiffs.addAll(gitDiffs); // equivalent to a for-each loop to add all gitDiffs
        this.setDiffs(allDiffs);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateCommitted(Date dateCommitted) {
        this.dateCommitted = dateCommitted;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNumAdditions(int numAdditions) {
        this.numAdditions = numAdditions;
    }

    public void setNumDeletions(int numDeletions) {
        this.numDeletions = numDeletions;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setDiffs(List<Diff> diffs) {
        this.diffs = diffs;
    }
}
