package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;

public class FileDto {
    String name;
    boolean isMergeRequestFile;
    String packageId;
    String[] unifiedDiff;
    Scores fileScore;
    LOCDto linesOfCodeChanges;

    boolean isIgnored;
    // might want projectId as well although im not sure?

    public FileDto(String[] unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    public FileDto(String name, String packageId, String[] unifiedDiff, double score) {
        this.name = name;
        this.packageId = packageId;
        this.unifiedDiff = unifiedDiff;
        this.isIgnored = false;
        this.fileScore.setTotalScore(score);
    }

    public void setMergeRquestFileScore(Scores fileScore, String packageId, boolean isMergeRequestFile) {
        this.isMergeRequestFile = isMergeRequestFile;
        this.packageId = packageId;
        this.fileScore = fileScore;
    }

    public void setTotalScore(double totalScore) {
        fileScore.setTotalScore(totalScore);
    }

    public void setLinesOfCodeChanges(LOCDto linesOfCodeChanges) {
        this.linesOfCodeChanges = linesOfCodeChanges;
    }

    public double getTotalScore() {
        return fileScore.getTotalScore();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageId(String commitId) {
        this.packageId = commitId;
    }

    public void setUnifiedDiff(String[] unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }
}
