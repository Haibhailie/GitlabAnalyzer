package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

import java.util.List;

public final class CommitDtoDb {
    private String id;
    private String message;
    private String author;
    private String authorEmail;
    private long time;
    private String webUrl;

    private int numAdditions;
    private int numDeletions;
    private int total;
    private String diffs;
    private boolean isIgnored;
    private List<FileDto> files;
    private double score;

    public CommitDtoDb(String jwt, ConfigService configService,Commit commit, List<Diff> diffList) {
        setId(commit.getId());
        setMessage(commit.getMessage());
        setAuthor(commit.getAuthorName());
        setAuthorEmail(commit.getAuthorEmail());
        setTime(commit.getCommittedDate().getTime());
        setWebUrl(commit.getWebUrl());

        setNumAdditions(commit.getStats().getAdditions());
        setNumDeletions(commit.getStats().getDeletions());
        setTotal(commit.getStats().getTotal());

        setDiffs(DiffStringParser.parseDiff(diffList));
        setIgnored(false);

        CommitScoreCalculator scoreCalculator = new CommitScoreCalculator(configService);
        List<FileDto> fileScores = scoreCalculator.getCommitScore(jwt, diffList);
        setFiles(fileScores);
        setScore(fileScores);
    }

    public CommitDtoDb() {
    }

    public CommitDtoDb setId(String id) {
        this.id = id;
        return this;
    }

    public CommitDtoDb setMessage(String message) {
        this.message = message;
        return this;
    }

    public CommitDtoDb setAuthor(String author) {
        this.author = author;
        return this;
    }

    public CommitDtoDb setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
        return this;
    }

    public CommitDtoDb setTime(long time) {
        this.time = time;
        return this;
    }

    public CommitDtoDb setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public CommitDtoDb setNumAdditions(int numAdditions) {
        this.numAdditions = numAdditions;
        return this;
    }

    public CommitDtoDb setNumDeletions(int numDeletions) {
        this.numDeletions = numDeletions;
        return this;
    }

    public CommitDtoDb setTotal(int total) {
        this.total = total;
        return this;
    }

    public CommitDtoDb setDiffs(String diffs) {
        this.diffs = diffs;
        return this;
    }

    public CommitDtoDb setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
        return this;
    }

    public CommitDtoDb setFiles(List<FileDto> files) {
        this.files = files;
        return this;
    }

    public CommitDtoDb setScore(double score) {
        this.score = score;
        return this;
    }

    public CommitDtoDb setScore(List<FileDto> files) {
        for (FileDto file : files) {
            this.score += file.getTotalScore();
        }
        return this;
    }

    public double getScore() {
        return score;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public long getTime() {
        return time;
    }

    public String getWebUrl() {
        return webUrl;
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

    public String getDiffs() {
        return diffs;
    }

    public boolean isIgnored() {
        return isIgnored;
    }

    public List<FileDto> getFiles() {
        return files;
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
                && this.message.equals(c.message)
                && this.author.equals(c.author)
                && this.authorEmail.equals(c.authorEmail)
                && this.time == c.time
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
