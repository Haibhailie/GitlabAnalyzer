package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
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
    private boolean isIgnored;
    private List<FileDto> files;
    private double score;

    public CommitDtoDb(Commit commit, List<Diff> diffList) {
        setId(commit.getId());
        setTitle(commit.getTitle());
        setMessage(commit.getMessage());
        setAuthorName(commit.getAuthorName());
        setAuthorEmail(commit.getAuthorEmail());
        setDateCommitted(commit.getCommittedDate().getTime());
        setWebUrl(commit.getWebUrl());

        setNumAdditions(commit.getStats().getAdditions());
        setNumDeletions(commit.getStats().getDeletions());
        setTotal(commit.getStats().getTotal());

        setDiffs(DiffStringParser.parseDiff(diffList));
        setIgnored(false);

        CommitScoreCalculator scoreCalculator = new CommitScoreCalculator();
        List<FileDto> fileScores = scoreCalculator.getCommitScore(diffList);
        setFiles(fileScores);
        setScore(fileScores);
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

    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public void setScore(List<FileDto> files) {
        for (FileDto file : files) {
            this.score += file.getTotalScore();
        }
    }

    public double getScore() {
        return score;
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
                && this.isIgnored == c.isIgnored
                && this.files.equals(c.files)
                && this.score == c.score);
    }
}
