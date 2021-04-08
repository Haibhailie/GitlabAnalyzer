package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestScoreCalculator;
import org.gitlab4j.api.models.MergeRequest;

import java.util.List;
import java.util.Set;

public final class MergeRequestDtoDb {
    private int id;
    private String title;
    private String author;
    private String description;
    private long mergedAt;
    private String webUrl;

    private List<CommitDtoDb> commits;
    private Set<String> committers;
    private double sumOfCommitsScore;
    private boolean isIgnored;
    private List<FileDto> files;

    public MergeRequestDtoDb(MergeRequest mergeRequest,
                             List<CommitDtoDb> commits,
                             Set<String> committers,
                             MergeRequest mergeRequestChanges,
                             double mergeRequestScore) {
        setId(mergeRequest.getIid());
        setTitle(mergeRequest.getTitle());
        setAuthor(mergeRequest.getAuthor().getName());
        setDescription(mergeRequest.getDescription());
        setMergedAt(mergeRequest.getMergedAt().getTime());
        setWebUrl(mergeRequest.getWebUrl());

        setCommits(commits);
        setCommitters(committers);
        setSumOfCommitsScore(mergeRequestScore);
        setIgnored(false);

        MergeRequestScoreCalculator scoreCalculator = new MergeRequestScoreCalculator();
        setFiles(scoreCalculator.getMergeRequestScore(mergeRequestChanges));
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMergedAt(long mergedAt) {
        this.mergedAt = mergedAt;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCommits(List<CommitDtoDb> commits) {
        this.commits = commits;
    }

    public void setCommitters(Set<String> committers) {
        this.committers = committers;
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

    public Set<String> getCommitters() {
        return committers;
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

        return (this.id == m.id
                && this.title.equals(m.title)
                && this.author.equals(m.author)
                && this.description.equals(m.description)
                && this.mergedAt == m.mergedAt
                && this.webUrl.equals(m.webUrl)
                && this.commits.equals(m.commits)
                && this.committers.equals(m.committers)
                && this.sumOfCommitsScore == m.sumOfCommitsScore
                && this.isIgnored == m.isIgnored
                && this.files.equals(m.files));
    }
}
