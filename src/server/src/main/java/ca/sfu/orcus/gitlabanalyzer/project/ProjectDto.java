package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.models.Project;

public class ProjectDto {
    private int id;
    private String name;
    private String role;
    private long lastActivityTime;
    private long lastAnalysisTime;
    private String webUrl;

    public ProjectDto(Project project, String role, long lastAnalysisTime) {
        setId(project.getId());
        setName(project.getName());
        setRole(role);
        setLastActivityTime(project.getLastActivityAt().getTime());
        setAnalyzed(lastAnalysisTime);
        setWebUrl(project.getWebUrl());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public void setAnalyzed(long lastAnalyzed) {
        this.lastAnalysisTime = lastAnalyzed;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public int getId() {
        return id;
    }

    public long getLastAnalysisTime() {
        return lastAnalysisTime;
    }

    public String getWebUrl() {
        return webUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ProjectDto)) {
            return false;
        }

        ProjectDto p = (ProjectDto) o;

        return (this.id == p.id
                && this.name.equals(p.name)
                && this.role.equals(p.role)
                && this.lastActivityTime == p.lastActivityTime
                && this.lastAnalysisTime == p.lastAnalysisTime)
                && this.webUrl.equals(p.webUrl);
    }
}