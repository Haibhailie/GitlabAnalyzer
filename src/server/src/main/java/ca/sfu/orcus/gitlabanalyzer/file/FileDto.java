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

    public FileDto(String name) {
        this.name = name;
        setExtension(name);
    }

    public FileDto(String name, List<FileDiffDto> fileDiffDtos, double score, boolean isIgnored) {
        this.name = name;
        setExtension(name);
        setFileDiffDtos(fileDiffDtos);
        this.isIgnored = isIgnored;
        this.setTotalScore(score);
    }

    public FileDto setExtension(String name) {
        if (name.contains(".")) {
            this.extension = name.substring(name.indexOf(".") + 1);
        } else {
            this.extension = "Unknown";
        }
        return this;
    }

    public FileDto setFileDiffDtos(List<FileDiffDto> fileDiffDtos) {
        this.fileDiffDtos = fileDiffDtos;
        return this;
    }

    public FileDto setTotalScore(Scores fileScore) {
        this.fileScore = fileScore;
        return this;
    }

    public FileDto setTotalScore(double totalScore) {
        fileScore.setTotalScore(totalScore);
        return this;
    }

    public FileDto setLinesOfCodeChanges(LOCDto linesOfCodeChanges) {
        this.linesOfCodeChanges = linesOfCodeChanges;
        return this;
    }

    public FileDto setIgnored(boolean ignored) {
        isIgnored = ignored;
        return this;
    }

    public boolean isIgnored() {
        return isIgnored;
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

    public String getFileName() {
        return name;
    }

    public String getFileExtension() {
        return extension;
    }

    public String getName() {
        return name;
    }

    public List<FileDiffDto> getFileDiffDtos() {
        return fileDiffDtos;
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
