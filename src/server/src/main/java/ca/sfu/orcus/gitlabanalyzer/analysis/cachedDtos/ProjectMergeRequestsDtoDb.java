package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import org.gitlab4j.api.models.Project;

import java.util.List;

public final class ProjectMergeRequestsDtoDb {
    private int id;
    private String webUrl;
    private List<MergeRequestDtoDb> mergeRequests;

    public ProjectMergeRequestsDtoDb(Project project, List<MergeRequestDtoDb> mergeRequests) {
        setId(project.getId());
        setWebUrl(project.getWebUrl());
        setMergeRequests(mergeRequests);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setMergeRequests(List<MergeRequestDtoDb> mergeRequests) {
        this.mergeRequests = mergeRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ProjectMergeRequestsDtoDb)) {
            return false;
        }

        ProjectMergeRequestsDtoDb p = (ProjectMergeRequestsDtoDb) o;

        return (this.id == p.id
                && this.webUrl.equals(p.webUrl)
                && this.mergeRequests.equals(p.mergeRequests));
    }
}