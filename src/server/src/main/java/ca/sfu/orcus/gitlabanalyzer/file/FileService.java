package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    private FileDto changeCommitFileScore(GitLabApi gitLabApi, int projectId, String commitId, String filePath, double score) {
        try {
            List<FileDiffDto> fileDiffDtos = retrieveDiffsForFile(gitLabApi, projectId, commitId, filePath);
            return new FileDto(filePath, fileDiffDtos, score, false);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public FileDto ignoreFile(String jwt, int projectId, String commitId, String filePath, double score) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return ignoreFile(gitLabApi, projectId, commitId, filePath, score);
    }

    private FileDto ignoreFile(GitLabApi gitLabApi, int projectId, String commitId, String filePath, double score) {
        try {
            List<FileDiffDto> fileDiffDtos = retrieveDiffsForFile(gitLabApi, projectId, commitId, filePath);
            FileDto file = new FileDto(filePath, fileDiffDtos, score, false);
            file.setIgnored(true);
            return file;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public FileDto unignoreFile(String jwt, int projectId, String commitId, String filePath, double score) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }

        FileDto file = ignoreFile(gitLabApi, projectId, commitId, filePath, score);
        if (file == null) {
            return null;
        }
        file.setIgnored(false);
        return file;
    }

    private List<FileDiffDto> retrieveDiffsForFile(GitLabApi gitLabApi, int projectId, String commitId, String filePath) throws GitLabApiException {
        List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, commitId);
        String[] diffString = DiffStringParser.parseDiff(diffList).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.parseDiffList(diffsList).getFileDiffs();
    }
}
