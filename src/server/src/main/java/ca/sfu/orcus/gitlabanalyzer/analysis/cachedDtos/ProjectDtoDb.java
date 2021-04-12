package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import org.gitlab4j.api.models.Project;

import java.util.List;

public class ProjectDtoDb {
    private int id;
    private String name;
    private String role;
    private long lastActivityTime;
    private long lastAnalysisTime;
    private long createdAt;
    private String webUrl;
    private List<CommitterDtoDb> committers;

    public ProjectDtoDb() {}

    public ProjectDtoDb(Project project, String role, long lastAnalysisTime, List<CommitterDtoDb> committers) {
        setId(project.getId());
        setName(project.getName());
        setRole(role);
        setLastActivityTime(project.getLastActivityAt().getTime());
        setLastAnalysisTime(lastAnalysisTime);
        setCreatedAt(project.getCreatedAt().getTime());
        setWebUrl(project.getWebUrl());
        setCommitters(committers);
    }

    public ProjectDtoDb setId(int id) {
        this.id = id;
        return this;
    }

    public ProjectDtoDb setName(String name) {
        this.name = name;
        return this;
    }

    public ProjectDtoDb setRole(String role) {
        this.role = role;
        return this;
    }

    public ProjectDtoDb setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
        return this;
    }

    public ProjectDtoDb setLastAnalysisTime(long lastAnalysisTime) {
        this.lastAnalysisTime = lastAnalysisTime;
        return this;
    }

    public ProjectDtoDb setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public ProjectDtoDb setCommitters(List<CommitterDtoDb> committers) {
        this.committers = committers;
        return this;
    }

    public ProjectDtoDb setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public long getLastAnalysisTime() {
        return lastAnalysisTime;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<CommitterDtoDb> getCommitters() {
        return committers;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ProjectDtoDb)) {
            return false;
        }

        ProjectDtoDb p = (ProjectDtoDb) o;

        return (this.id == p.id
                && this.name.equals(p.name)
                && this.role.equals(p.role)
                && this.lastActivityTime == p.lastActivityTime
                && this.lastAnalysisTime == p.lastAnalysisTime
                && this.webUrl.equals(p.webUrl)
                && this.committers.equals(p.committers));
    }
}
