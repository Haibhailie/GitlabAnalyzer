package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;

import java.util.ArrayList;
import java.util.List;

public class FileDto {
    String name;

    boolean isIgnored;
    Scores fileScore;
    LOCDto linesOfCodeChanges;
    List<FileDiffDto> fileDiffDtos = new ArrayList<>();

    public FileDto(String[] unifiedDiff, String name) {
        this.name = name;
        generateFileDiffDto(unifiedDiff);
    }

    public FileDto(String name, String[] unifiedDiff, double score) {
        this.name = name;
        generateFileDiffDto(unifiedDiff);
        this.isIgnored = false;
        this.setTotalScore(score);
    }

    public void generateFileDiffDto(String[] unifiedDiff) {
        for (String line : unifiedDiff) {
            fileDiffDtos.add(new FileDiffDto(line));
        }
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
