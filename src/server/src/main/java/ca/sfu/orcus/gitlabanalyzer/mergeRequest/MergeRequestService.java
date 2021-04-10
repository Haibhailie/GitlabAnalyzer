package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MergeRequestService {
    private final MergeRequestRepository mergeRequestRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private final ConfigService configService;
    private final CommitService commitService;

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper, ConfigService configService, CommitService commitService) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.configService = configService;
        this.commitService = commitService;
    }

    public List<MergeRequestDto> getAllMergeRequests(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        } else {
            return returnAllMergeRequests(jwt, gitLabApi, projectId, since, until);
        }
    }

    private List<MergeRequestDto> returnAllMergeRequests(String jwt, GitLabApi gitLabApi, int projectId, Date since, Date until) {
        try {
            List<MergeRequestDto> filteredMergeRequests = new ArrayList<>();
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
            for (MergeRequest mr : allMergeRequests) {
                if (mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until)) {
                    createAndStoreDto(jwt, gitLabApi, projectId, filteredMergeRequests, mr);
                }
            }
            return filteredMergeRequests;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public List<MergeRequestDto> returnAllMergeRequests(String jwt, GitLabApi gitLabApi, int projectId, Date since, Date until, int memberId) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            List<MergeRequestDto> filteredMergeRequests = new ArrayList<>();
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
            for (MergeRequest mr : allMergeRequests) {
                if (mr.getAuthor().getId() == memberId && mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until)) {
                    createAndStoreDto(jwt, gitLabApi, projectId, filteredMergeRequests, mr);
                }
            }
            return filteredMergeRequests;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private void createAndStoreDto(String jwt, GitLabApi gitLabApi, int projectId, List<MergeRequestDto> filteredMergeRequests, MergeRequest mr) throws GitLabApiException {
        List<FileDto> fileScores = setMergeRequestScores(jwt, gitLabApi, projectId, mr.getId());
        List<MergeRequestCommitsDto> mrCommits = new ArrayList<>();
        List<Commit> commits = gitLabApi.getMergeRequestApi().getCommits(projectId, mr.getId());
        double sumOfCommitsScore = getSumOfCommitsScore(jwt, gitLabApi, projectId, commits, mrCommits);

        MergeRequestDto presentMergeRequest = new MergeRequestDto(gitLabApi, projectId, mr, fileScores, sumOfCommitsScore, mrCommits);
        filteredMergeRequests.add(presentMergeRequest);
    }

    private List<FileDto> setMergeRequestScores(String jwt, GitLabApi gitLabApi, int projectId, int mergeRequestId) throws GitLabApiException {
        return getMergeRequestScore(jwt, gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId));
    }

    public List<FileDto> getMergeRequestScore(String jwt, MergeRequest mergeRequestChanges) {
        // regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(jwt, configService, diffsList);
    }

    public List<CommitDto> getAllCommitsFromMergeRequest(String jwt, int projectId, int mergeRequestId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return returnAllCommitsFromMergeRequest(jwt, gitLabApi, projectId, mergeRequestId);
        } else {
            return null;
        }
    }

    private List<CommitDto> returnAllCommitsFromMergeRequest(String jwt, GitLabApi gitLabApi, int projectId, int mergeRequestId) {
        try {
            List<CommitDto> filteredCommits = new ArrayList<>();
            List<Commit> allCommits = gitLabApi.getMergeRequestApi().getCommits(projectId, mergeRequestId);
            for (Commit c : allCommits) {
                List<Diff> diffs = gitLabApi.getCommitsApi().getDiff(projectId, c.getId());
                List<FileDto> fileScores = commitService.getCommitScore(jwt, diffs);
                filteredCommits.add(new CommitDto(gitLabApi, projectId, c, fileScores));
            }
            return filteredCommits;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public String getDiffFromMergeRequest(String jwt, int projectId, int mergeRequestId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return returnDiffFromMergeRequest(gitLabApi, projectId, mergeRequestId);
        } else {
            return null;
        }
    }

    private String returnDiffFromMergeRequest(GitLabApi gitLabApi, int projectId, int mergeRequestId) {
        try {
            MergeRequest mr = gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId);
            return DiffStringParser.parseDiff(mr.getChanges());
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public double getSumOfCommitsScore(String jwt, GitLabApi gitLabApi, int projectId, List<Commit> commits, List<MergeRequestCommitsDto> commitsInfoInMergeRequest) throws GitLabApiException {
        double sumOfCommitsScore = 0;
        for (Commit c : commits) {
            Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectId, c.getShortId());
            if (presentCommit.getStats() != null) {
                List<FileDto> presentCommitFiles = commitService.getCommitScore(jwt, gitLabApi.getCommitsApi().getDiff(projectId, presentCommit.getShortId()));
                for (FileDto fileIterator : presentCommitFiles) {
                    sumOfCommitsScore += fileIterator.getTotalScore();
                    commitsInfoInMergeRequest.add(new MergeRequestCommitsDto(fileIterator.getFileScore(), fileIterator.getLinesOfCodeChanges()));
                }
            }
        }
        return sumOfCommitsScore;
    }
}
