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
    private String singleLineComment;
    private String multiLineCommentStart;
    private String multiLineCommentEnd;
    private String syntaxInCode;
    private String yAxis;

    private ConfigDto() {
    }

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

    public void setSingleLineComment(String singleLineComment) {
        this.singleLineComment = singleLineComment;
    }

    public void setMultiLineCommentStart(String multiLineCommentStart) {
        this.multiLineCommentStart = multiLineCommentStart;
    }

    public void setMultiLineCommentEnd(String multiLineCommentEnd) {
        this.multiLineCommentEnd = multiLineCommentEnd;
    }

    public void setSyntaxInCode(String syntaxInCode) {
        this.syntaxInCode = syntaxInCode;
    }


    public String getName() {
        return name;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getScoreBy() {
        return scoreBy;
    }

    public String getGraphMode() {
        return graphMode;
    }

    public String getSingleLineComment() {
        return singleLineComment;
    }

    public String getMultiLineCommentStart() {
        return multiLineCommentStart;
    }

    public String getMultiLineCommentEnd() {
        return multiLineCommentEnd;
    }

    public String getSyntaxInCode() {
        return syntaxInCode;
    }

    public String getyAxis() {
        return yAxis;
    }

    public List<GeneralTypeScoreDto> getGeneralScores() {
        return generalScores;
    }

    public List<FileTypeScoreDto> getFileScores() {
        return fileScores;
    }

    // Nested class types

    public static final class FileTypeScoreDto {
        private String fileExtension;
        private double scoreMultiplier;

        public FileTypeScoreDto() {
        }

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

        public GeneralTypeScoreDto() {
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public GeneralTypeScoreDto getTypeScore() {
            return this;
        }
    }

}