package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import org.gitlab4j.api.models.Project;

import java.util.List;

public class ProjectDtoDb {
    private int id;
    private String name;
    private String role;
    private long lastActivityTime;
    private long lastAnalysisTime;
    private String webUrl;
    private List<CommitterDtoDb> committers;
    private List<MemberDtoDb> members;

    // TODO: Store currentConfig used for scoring

    public ProjectDtoDb(Project project,
                        String role,
                        long lastAnalysisTime,
                        List<CommitterDtoDb> committers,
                        List<MemberDtoDb> members) {
        setId(project.getId());
        setName(project.getName());
        setRole(role);
        setLastActivityTime(project.getLastActivityAt().getTime());
        setLastAnalysisTime(lastAnalysisTime);
        setWebUrl(project.getWebUrl());
        setCommitters(committers);
        setMembers(members);
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

    public void setLastAnalysisTime(long lastAnalysisTime) {
        this.lastAnalysisTime = lastAnalysisTime;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCommitters(List<CommitterDtoDb> committers) {
        this.committers = committers;
    }

    public void setMembers(List<MemberDtoDb> members) {
        this.members = members;
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
                && this.members.equals(p.members)
                && this.committers.equals(p.committers));
    }
}
