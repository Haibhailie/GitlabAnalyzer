package ca.sfu.orcus.gitlabanalyzer.config;

final class FileTypeScoreDto {
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