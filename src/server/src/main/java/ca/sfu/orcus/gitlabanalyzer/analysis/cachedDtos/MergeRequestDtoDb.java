package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestScoreCalculator;
import org.gitlab4j.api.models.MergeRequest;

import java.util.List;
import java.util.Set;

public final class MergeRequestDtoDb {
    private int mergeRequestId;
    private String title;
    private String author;
    private int authorId;
    private String description;
    private long time;
    private String webUrl;

    private List<CommitDtoDb> commits;
    private Set<String> committerNames;
    private double sumOfCommitsScore;
    private boolean isIgnored;
    private List<FileDto> files;

    public MergeRequestDtoDb() {
    }

    public MergeRequestDtoDb(MergeRequest mergeRequest,
                             List<CommitDtoDb> commits,
                             Set<String> committerNames,
                             MergeRequest mergeRequestChanges,
                             double sumOfCommitsScore) {
        setMergeRequestId(mergeRequest.getIid());
        setTitle(mergeRequest.getTitle());
        setAuthor(mergeRequest.getAuthor().getName());
        setAuthorId(mergeRequest.getAuthor().getId());
        setDescription(mergeRequest.getDescription());
        setTime(mergeRequest.getMergedAt().getTime());
        setWebUrl(mergeRequest.getWebUrl());

        setCommits(commits);
        setCommitterNames(committerNames);
        setSumOfCommitsScore(sumOfCommitsScore);
        setIgnored(false);

        MergeRequestScoreCalculator scoreCalculator = new MergeRequestScoreCalculator();
        setFiles(scoreCalculator.getMergeRequestScore(mergeRequestChanges));
    }

    public void setMergeRequestId(int mergeRequestId) {
        this.mergeRequestId = mergeRequestId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCommits(List<CommitDtoDb> commits) {
        this.commits = commits;
    }

    public void setCommitterNames(Set<String> committerNames) {
        this.committerNames = committerNames;
    }

    public void setSumOfCommitsScore(double sumOfCommitsScore) {
        this.sumOfCommitsScore = sumOfCommitsScore;
    }

    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getMergeRequestId() {
        return mergeRequestId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public long getTime() {
        return time;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<CommitDtoDb> getCommits() {
        return commits;
    }

    public Set<String> getCommitterNames() {
        return committerNames;
    }

    public double getSumOfCommitsScore() {
        return sumOfCommitsScore;
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

        if (!(o instanceof MergeRequestDtoDb)) {
            return false;
        }

        MergeRequestDtoDb m = (MergeRequestDtoDb) o;

        return (this.mergeRequestId == m.mergeRequestId
                && this.title.equals(m.title)
                && this.author.equals(m.author)
                && this.authorId == m.authorId
                && this.description.equals(m.description)
                && this.time == m.time
                && this.webUrl.equals(m.webUrl)
                && this.commits.equals(m.commits)
                && this.committerNames.equals(m.committerNames)
                && this.sumOfCommitsScore == m.sumOfCommitsScore
                && this.isIgnored == m.isIgnored
                && this.files.equals(m.files));
    }
}
