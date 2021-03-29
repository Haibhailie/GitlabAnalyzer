package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;

public class FileDto {
    String name;

    boolean isIgnored;
    Scores fileScore;
    LOCDto linesOfCodeChanges;

    //Another object
    String[] unifiedDiff;

    public FileDto(String[] unifiedDiff, String name) {
        this.name = name;
        this.unifiedDiff = unifiedDiff;
    }

    public FileDto(String name, String[] unifiedDiff, double score) {
        this.name = name;
        this.unifiedDiff = unifiedDiff;
        this.isIgnored = false;
        this.setTotalScore(score);
    }

    public void setMergeRquestFileScore(Scores fileScore) {
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

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

}
