package ca.sfu.orcus.gitlabanalyzer.config;

public final class ConfigIdDto {
    private String id;

    private ConfigIdDto() {}

    public ConfigIdDto(String id) {
        setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
