package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MergeRequestService {
    private final MergeRequestRepository mergeRequestRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<MergeRequestDtoDb> getAllMergeRequests(String jwt, int projectId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        } else {
            Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
            return projectUrl.map(mergeRequestRepository::getAllMergeRequests).orElse(null);
        }
    }

    public void updateMergeRequestIgnore(String projectUrl, int mergeRequestId, boolean doIgnore) {
        mergeRequestRepository.updateMergeRequestIgnore(projectUrl, mergeRequestId, doIgnore);
    }

    public void updateMergeRequestFileIgnore(String projectUrl, int mergerequestId, String fileId, boolean doIgnore) {
        mergeRequestRepository.updateMergeRequestFileIgnore(projectUrl, mergerequestId, fileId, doIgnore);
    }

    public void updateCommitIgnore(String projectUrl, int mergeRequestId, String commitId, boolean doIgnore) {
        mergeRequestRepository.updateCommitIgnore(projectUrl, mergeRequestId, commitId, doIgnore);
    }
}
