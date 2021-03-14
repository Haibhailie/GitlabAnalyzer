package ca.sfu.orcus.gitlabanalyzer.config;

import java.util.List;

final class ConfigDto {
    private int id;

    private String name;
    private long startDate;
    private long endDate;
    private String scoreBy;
    private String yAxis;
    private String graphMode;
    private List<GeneralTypeScoreDto> generalScores;
    private List<FileTypeScoreDto> fileScores;

    public ConfigDto(String name,
                     long startDate,
                     long endDate,
                     String scoreBy,
                     String yAxis,
                     String graphMode,
                     List<GeneralTypeScoreDto> generalScores,
                     List<FileTypeScoreDto> fileScores) {
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
        setScoreBy(scoreBy);
        setYAxis(yAxis);
        setGraphMode(graphMode);
        setGeneralScores(generalScores);
        setFileScores(fileScores);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setScoreBy(String scoreBy) {
        this.scoreBy = scoreBy;
    }

    public void setYAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public void setGraphMode(String graphMode) {
        this.graphMode = graphMode;
    }

    public void setGeneralScores(List<GeneralTypeScoreDto> generalScores) {
        this.generalScores = generalScores;
    }

    public void setFileScores(List<FileTypeScoreDto> fileScores) {
        this.fileScores = fileScores;
    }
}