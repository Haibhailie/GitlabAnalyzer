package ca.sfu.orcus.gitlabanalyzer.config;

final class GeneralTypeScoreDto {
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
