package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final GitLabApiWrapper gitLabApiWrapper;
    private final FileRepository fileRepository;

    @Autowired
    public FileService(GitLabApiWrapper gitLabApiWrapper, FileRepository fileRepository) {
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.fileRepository = fileRepository;
    }

    public FileDto changeCommitFileScore(String jwt, int projectId, String commitId, String filePath, double score) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return changeCommitFileScore(gitLabApi, projectId, commitId, filePath, score);
    }

    public FileDto changeCommitFileScore(GitLabApi gitLabApi, int projectId, String commitId, String filePath, double score) {
        try {
            String[] arr = addDiffs(gitLabApi, projectId, commitId, filePath);
            return new FileDto(filePath, arr, score);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public FileDto changeFileIgnoreTrue(String jwt, int projectId, String commitId, String filePath, double score) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return changeFileIgnoreTrue(gitLabApi, projectId, commitId, filePath, score);
    }

    public FileDto changeFileIgnoreTrue(GitLabApi gitLabApi, int projectId, String commitId, String filePath, double score) {
        try {
            String[] arr = addDiffs(gitLabApi, projectId, commitId, filePath);
            FileDto file = new FileDto(filePath, arr, score);
            file.setIgnored(true);
            return file;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public FileDto changeFileIgnoreFalse(String jwt, int projectId, String commitId, String filePath, double score) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        FileDto file = changeFileIgnoreTrue(gitLabApi, projectId, commitId, filePath, score);
        file.setIgnored(false);
        return file;
    }

    private String[] addDiffs(GitLabApi gitLabApi, int projectId, String commitId, String filePath) throws GitLabApiException {
        List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, commitId);
        String[] arr = new String[diffList.size()];
        int i = 0;
        for (Diff d : diffList) {
            if (d.getNewPath().equals(filePath)) {
                arr[i] = d.getDiff();
                i++;
            }
        }
        return arr;
    }
}
