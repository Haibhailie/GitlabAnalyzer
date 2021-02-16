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
        this.setId(commit.getId());
        this.setDateCommitted(commit.getCommittedDate());
        this.setMessage(commit.getMessage());
        this.setNumAdditions(commit.getStats().getAdditions());
        this.setNumDeletions(commit.getStats().getDeletions());
        this.setTotal(commit.getStats().getTotal());

        List<Diff> allDiffs = new ArrayList<>();
        List<Diff> gitDiffs = gitLabApi.getCommitsApi().getDiff(projectID, commit.getId());
        if(!gitDiffs.isEmpty()) {
            for(Diff d : gitDiffs) {
                allDiffs.add(d);
            }
            this.setDiffs(allDiffs);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public Date getDateCommitted() {
        return dateCommitted;
    }

    public String getMessage() {
        return message;
    }

    public int getNumAdditions() {
        return numAdditions;
    }

    public int getNumDeletions() {
        return numDeletions;
    }

    public int getTotal() {
        return total;
    }

    public List<Diff> getDiffs() {
        return diffs;
    }
}
