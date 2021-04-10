package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommitService {
    private final CommitRepository commitRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private final ConfigService configService;

    @Autowired
    public CommitService(CommitRepository commitRepository, GitLabApiWrapper gitLabApiWrapper, ConfigService configService) {
        this.commitRepository = commitRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.configService = configService;
    }

    public List<CommitDto> getAllCommits(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return getAllCommitDtos(jwt, gitLabApi, projectId, since, until);
    }

    private List<CommitDto> getAllCommitDtos(String jwt, GitLabApi gitLabApi, int projectId, Date since, Date until) {
        try {
            String defaultBranch = gitLabApi.getProjectApi().getProject(projectId).getDefaultBranch();
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until);
            List<CommitDto> allCommits = new ArrayList<>();
            for (Commit commit : allGitCommits) {
                List<Diff> diffs = gitLabApi.getCommitsApi().getDiff(projectId, commit.getId());
                List<FileDto> fileScores = getCommitScore(jwt, diffs);
                CommitDto presentCommit = new CommitDto(gitLabApi, projectId, commit, fileScores);
                allCommits.add(presentCommit);
            }
            return allCommits;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public List<FileDto> getCommitScore(String jwt, List<Diff> diffs) {
        // regex to split lines by new line and store in generatedDiffList
        String[] diffArray = DiffStringParser.parseDiff(diffs).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffArray);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(jwt, configService, diffsList);
    }

    public List<CommitDto> returnAllCommitsOfAMember(String jwt, GitLabApi gitLabApi, int projectId, Date since, Date until, String name) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            String defaultBranch = gitLabApi.getProjectApi().getProject(projectId).getDefaultBranch();
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until);
            List<CommitDto> allCommits = new ArrayList<>();
            for (Commit commit : allGitCommits) {
                if (commit.getAuthorName().equalsIgnoreCase(name)) {
                    List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, commit.getId());
                    List<FileDto> fileScores = getCommitScore(jwt, diffList);
                    CommitDto presentCommit = new CommitDto(gitLabApi, projectId, commit, fileScores);
                    allCommits.add(presentCommit);
                }
            }
            return allCommits;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public CommitDto getSingleCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        try {
            Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
            List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, gitCommit.getId());
            List<FileDto> fileScores = getCommitScore(jwt, diffList);
            return new CommitDto(gitLabApi, projectId, gitCommit, fileScores);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public String getDiffOfCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        CommitDto commitDto = getSingleCommit(jwt, projectId, sha);
        return commitDto.getDiffs();
    }
}
