package ca.sfu.orcus.gitlabanalyzer.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

final class ConfigDto {
    private String id;
    private String name;
    private long startDate;
    private long endDate;
    private String scoreBy;

    @SerializedName(value = "yAxis")
    private String verticalAxis;

    private String graphMode;
    private List<GeneralTypeScoreDto> generalScores;
    private List<FileTypeScoreDto> fileScores;

    public ConfigDto(String id,
                     String name,
                     long startDate,
                     long endDate,
                     String scoreBy,
                     String verticalAxis,
                     String graphMode,
                     List<GeneralTypeScoreDto> generalScores,
                     List<FileTypeScoreDto> fileScores) {
        setId(id);
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
        setScoreBy(scoreBy);
        setVerticalAxis(verticalAxis);
        setGraphMode(graphMode);
        setGeneralScores(generalScores);
        setFileScores(fileScores);
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

    public void setVerticalAxis(String verticalAxis) {
        this.verticalAxis = verticalAxis;
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

    private static final class FileTypeScoreDto {
        private String fileExtension;
        private int scoreMultiplier;

        FileTypeScoreDto(String fileExtension, int scoreMultiplier) {
            setFileExtension(fileExtension);
            setScoreMultiplier(scoreMultiplier);
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public void setScoreMultiplier(int scoreMultiplier) {
            this.scoreMultiplier = scoreMultiplier;
        }
    }

    private static final class GeneralTypeScoreDto {
        private String type;
        private int value;

        public GeneralTypeScoreDto(String type, int value) {
            setType(type);
            setValue(value);
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}