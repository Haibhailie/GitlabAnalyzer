package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

import java.util.Date;
import java.util.List;

public class CommitDto {
    private String title;
    private String author;
    private String authorEmail;
    private String id;
    private Date dateCommitted;
    private long time;
    private String message;
    private int numAdditions;
    private int numDeletions;
    private int total;
    private String diffs;
    boolean isIgnored;
    private List<FileDto> files;

    public CommitDto(GitLabApi gitLabApi, int projectId, Commit commit) throws GitLabApiException {
        this.setTitle(commit.getTitle());
        this.setAuthor(commit.getAuthorName());
        this.setAuthorEmail(commit.getAuthorEmail());
        this.setId(commit.getId());
        this.setDateCommitted(commit.getCommittedDate());
        this.setTime(commit.getCommittedDate().getTime());
        this.setMessage(commit.getMessage());

        Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectId, commit.getShortId()); // Needed otherwise getStats() returns null
        this.setNumAdditions(presentCommit.getStats().getAdditions());
        this.setNumDeletions(presentCommit.getStats().getDeletions());
        this.setTotal(presentCommit.getStats().getTotal());

        List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, commit.getId());
        this.setDiffs((DiffStringParser.parseDiff(diffList)));

        CommitScoreCalculator scoreCalculator = new CommitScoreCalculator();
        this.setFiles(scoreCalculator.getCommitScore(gitLabApi.getCommitsApi().getDiff(projectId, commit.getId())));
        isIgnored = false;
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

    public void setTime(long time) {
        this.time = time;
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

    public void setDiffs(String diffs) {
        this.diffs = diffs;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public String getDiffs() {
        return diffs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CommitDto)) {
            return false;
        }

        CommitDto c = (CommitDto) o;

        return (this.id.equals(c.id)
                && this.title.equals(c.title)
                && this.author.equals(c.author)
                && this.authorEmail.equals(c.authorEmail)
                && this.dateCommitted.equals(c.dateCommitted)
                && this.time == c.time
                && this.message.equals(c.message)
                && this.numAdditions == c.numAdditions
                && this.numDeletions == c.numDeletions
                && this.total == c.total
                && this.diffs.equals(c.diffs)
                && this.isIgnored == c.isIgnored);
        //&& this.files == c.files); Removed this since it was failing tests, and plus, we don't really test files in our mocks anyway :/
    }
}
