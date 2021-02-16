package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Owner;
import org.gitlab4j.api.models.Project;

import java.util.Date;

public class ProjectDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Owner owner;
    private Date lastActivityAt;
    private Date createdAt;
    private String defaultBranch;
    private long storageSize;

    public ProjectDto(GitLabApi gitLabApi, Project project) throws GitLabApiException {
        setId(project.getId());
        setName(project.getName());
        setDescription(project.getDescription());
        setPublic(project.getPublic());
        setOwner(project.getOwner());
        setLastActivityAt(project.getLastActivityAt());
        setCreatedAt(project.getCreatedAt());
        setDefaultBranch(project.getDefaultBranch());
        setStorageSize(project.getStatistics().getStorageSize());
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setLastActivityAt(Date lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public void setStorageSize(long storageSize) {
        this.storageSize = storageSize;
    }
}
