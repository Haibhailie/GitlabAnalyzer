package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FileDto {
    String name;
    String extension;
    boolean isIgnored;
    Scores fileScore;
    LOCDto linesOfCodeChanges;

    @SerializedName("fileDiffs")
    List<FileDiffDto> fileDiffDtos = new ArrayList<>();

    public FileDto(String[] unifiedDiff, String name) {
        this.name = name;
        setExtension(name);
        generateFileDiffDto(unifiedDiff);
    }

    public FileDto(String name, String[] unifiedDiff, double score, boolean isIgnored) {
        this.name = name;
        setExtension(name);
        generateFileDiffDto(unifiedDiff);
        this.isIgnored = isIgnored;
        this.setTotalScore(score);
    }

    public void setExtension(String name) {
        if (name.contains(".")) {
            extension = name.substring(name.indexOf("."));
        } else {
            extension = "Unknown";
        }
    }

    public void setFileDiffDtos(List<FileDiffDto> fileDiffDtos) {
        this.fileDiffDtos = fileDiffDtos;
    }

    private void generateFileDiffDto(String[] unifiedDiff) {
        for (String line : unifiedDiff) {
            fileDiffDtos.add(new FileDiffDto(line));
        }
    }

    public void setMergeRequestFileScore(Scores fileScore) {
        this.fileScore = fileScore;
    }

    public void setTotalScore(double totalScore) {
        fileScore.setTotalScore(totalScore);
    }

    public void setLinesOfCodeChanges(LOCDto linesOfCodeChanges) {
        this.linesOfCodeChanges = linesOfCodeChanges;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public double getTotalScore() {
        return fileScore.getTotalScore();
    }

    public Scores getFileScore() {
        return fileScore;
    }

    public LOCDto getLinesOfCodeChanges() {
        return linesOfCodeChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FileDto)) {
            return false;
        }

        FileDto m = (FileDto) o;

        return (this.name.equals(m.name)
                && this.extension.equals(m.extension)
                && this.isIgnored == m.isIgnored
                && this.fileScore.equals(m.fileScore)
                && this.linesOfCodeChanges.equals(m.linesOfCodeChanges))
                && this.fileDiffDtos.equals(m.fileDiffDtos);
    }
}
