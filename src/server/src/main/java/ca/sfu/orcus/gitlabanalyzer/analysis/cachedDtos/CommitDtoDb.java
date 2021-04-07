package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

import java.util.List;

public final class CommitDtoDb {
    private String id;
    private String title;
    private String message;
    private String authorName;
    private String authorEmail;
    private long dateCommitted;
    private String webUrl;

    private int numAdditions;
    private int numDeletions;
    private int total;
    private String diffs;
    private double score;

    public CommitDtoDb(Commit commit, List<Diff> diffList) {
        this.setId(commit.getId());
        this.setTitle(commit.getTitle());
        this.setMessage(commit.getMessage());
        this.setAuthorName(commit.getAuthorName());
        this.setAuthorEmail(commit.getAuthorEmail());
        this.setDateCommitted(commit.getCommittedDate().getTime());
        this.setWebUrl(commit.getWebUrl());

        this.setNumAdditions(commit.getStats().getAdditions());
        this.setNumDeletions(commit.getStats().getDeletions());
        this.setTotal(commit.getStats().getTotal());

        this.setDiffs(DiffStringParser.parseDiff(diffList));

        CommitScoreCalculator scoreCalculator = new CommitScoreCalculator();
        double commitScore = scoreCalculator.getCommitScore(diffList);
        this.setScore(commitScore);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public void setDateCommitted(long dateCommitted) {
        this.dateCommitted = dateCommitted;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
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

    public void setScore(double score) {
        this.score = score;
    }

    public int getNumAdditions() {
        return numAdditions;
    }

    public int getNumDeletions() {
        return numDeletions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof CommitDtoDb)) {
            return false;
        }

        CommitDtoDb c = (CommitDtoDb) o;

        return (this.id.equals(c.id)
                && this.title.equals(c.title)
                && this.message.equals(c.message)
                && this.authorName.equals(c.authorName)
                && this.authorEmail.equals(c.authorEmail)
                && this.dateCommitted == c.dateCommitted
                && this.webUrl.equals(c.webUrl)
                && this.numAdditions == c.numAdditions
                && this.numDeletions == c.numDeletions
                && this.total == c.total
                && this.diffs.equals(c.diffs)
                && this.score == c.score);
    }
}
