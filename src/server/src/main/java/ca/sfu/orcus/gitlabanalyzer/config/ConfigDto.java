package ca.sfu.orcus.gitlabanalyzer.config;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class ConfigDto {
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

    public void setId(String id) {
        this.id = id;
    }

    // Nested class types

    private static final class FileTypeScoreDto {
        private String fileExtension;
        private int scoreMultiplier;

        private FileTypeScoreDto() {}
    }

    private static final class GeneralTypeScoreDto {
        private String type;
        private int value;

        private GeneralTypeScoreDto() {}
    }
}