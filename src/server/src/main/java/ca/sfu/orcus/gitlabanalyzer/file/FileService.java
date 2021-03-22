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
        FileDto file = new FileDto();
        file.commitId = commitId;
        file.isIgnored = false;
        file.path = filePath;
        file.mergeRequestId = null;
        try {
            List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, commitId);
            for (Diff d : diffList) {
                if (d.getNewPath().equals(filePath)) {
                    file.diff = d.getDiff();
                }
            }
        } catch (GitLabApiException e) {
            return null;
        }
        file.score = score;
        return file;
    }
}
