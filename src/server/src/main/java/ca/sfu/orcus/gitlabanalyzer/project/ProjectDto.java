package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.models.Project;

public class ProjectDto {
    private int id;
    private String name;
    private String role;
    private long lastActivityAt;
    private boolean analyzed;

    public ProjectDto(Project project, String role) {
        setId(project.getId());
        setName(project.getName());
        setRole(role);
        setLastActivityAt(project.getLastActivityAt().getTime());
        setAnalyzed(false); // TODO: Iteration 2
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

    public void setLastActivityAt(long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }
}