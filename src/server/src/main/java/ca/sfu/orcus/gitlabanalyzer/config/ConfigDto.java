package ca.sfu.orcus.gitlabanalyzer.config;

import java.util.List;

public final class ConfigDto {
    private String id;
    private String name;
    private long startDate;
    private long endDate;
    private String scoreBy;
    private String graphMode;

    private List<GeneralTypeScoreDto> generalScores;
    private List<FileTypeScoreDto> fileScores;
    private String yAxis;

    private ConfigDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setGraphMode(String graphMode) {
        this.graphMode = graphMode;
    }

    public void setGeneralScores(List<GeneralTypeScoreDto> generalScores) {
        this.generalScores = generalScores;
    }

    public void setFileScores(List<FileTypeScoreDto> fileScores) {
        this.fileScores = fileScores;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public List<GeneralTypeScoreDto> getGeneralScores() {
        return generalScores;
    }

    // Nested class types

    private static final class FileTypeScoreDto {
        private String fileExtension;
        private double scoreMultiplier;

        private FileTypeScoreDto() {}

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public void setScoreMultiplier(double scoreMultiplier) {
            this.scoreMultiplier = scoreMultiplier;
        }
    }

    public static final class GeneralTypeScoreDto {
        private String type;
        private double value;

        private GeneralTypeScoreDto() {}

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public double getValue() {
            return value;
        }
    }
}