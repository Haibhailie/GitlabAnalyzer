package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import java.util.List;

public final class ProjectMergeRequestsDtoDb {
    private String webUrl;
    private List<MergeRequestDtoDb> mergeRequests;

    public ProjectMergeRequestsDtoDb(String webUrl, List<MergeRequestDtoDb> mergeRequests) {
        setWebUrl(webUrl);
        setMergeRequests(mergeRequests);
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

        return (this.webUrl.equals(p.webUrl)
                && this.mergeRequests.equals(p.mergeRequests));
    }
}