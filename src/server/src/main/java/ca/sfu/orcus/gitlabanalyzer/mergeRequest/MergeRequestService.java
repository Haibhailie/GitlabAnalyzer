package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MergeRequestService {
    private final MergeRequestRepository mergeRequestRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private final ConfigService configService;

    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper, ConfigService configService) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.configService = configService;
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
                    List<FileDto> fileScores = setMergeRequestScores(jwt, gitLabApi, projectId, mr.getId());
                    MergeRequestDto presentMergeRequest = new MergeRequestDto(jwt, gitLabApi, projectId, mr, fileScores);
                    filteredMergeRequests.add(presentMergeRequest);
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
                    List<FileDto> fileScores = setMergeRequestScores(jwt, gitLabApi, projectId, mr.getId());
                    MergeRequestDto presentMergeRequest = new MergeRequestDto(jwt, gitLabApi, projectId, mr, fileScores);
                    filteredMergeRequests.add(presentMergeRequest);
                }
            }
            return filteredMergeRequests;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private List<FileDto> setMergeRequestScores(String jwt, GitLabApi gitLabApi, int projectId, int mergeRequestId) throws GitLabApiException {
        return getMergeRequestScore(jwt, gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId));
    }

    public List<FileDto> getMergeRequestScore(String jwt, MergeRequest mergeRequestChanges) {
        setMultipliersFromConfig(jwt);

        // regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(diffsList, addLOCFactor, deleteLOCFactor, syntaxChangeFactor, blankLOCFactor, spacingChangeFactor);
    }

    private void setMultipliersFromConfig(String jwt) {
        try {
            Optional<ConfigDto> configDto = configService.getCurrentConfig(jwt);
            if (configDto.isPresent()) {
                List<ConfigDto.GeneralTypeScoreDto> list = configDto.get().getGeneralScores();
                for (ConfigDto.GeneralTypeScoreDto g : list) {
                    switch (g.getType()) {
                        case "addLoc" -> addLOCFactor = g.getValue();
                        case "deleteLoc" -> deleteLOCFactor = g.getValue();
                        case "Syntax" -> syntaxChangeFactor = g.getValue();
                        case "blank" -> blankLOCFactor = g.getValue();
                        case "spacing" -> spacingChangeFactor = g.getValue();
                        default -> throw new IllegalStateException("Unexpected value: " + g.getType());
                    }
                }
            }
        } catch (GitLabApiException e) {
            // default multipliers
        }
    }

    public List<CommitDto> getAllCommitsFromMergeRequest(String jwt, int projectId, int mergeRequestId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return returnAllCommitsFromMergeRequest(gitLabApi, projectId, mergeRequestId);
        } else {
            return null;
        }
    }

    private List<CommitDto> returnAllCommitsFromMergeRequest(GitLabApi gitLabApi, int projectId, int mergeRequestId) {
        try {
            List<CommitDto> filteredCommits = new ArrayList<>();
            List<Commit> allCommits = gitLabApi.getMergeRequestApi().getCommits(projectId, mergeRequestId);
            for (Commit c : allCommits) {
                filteredCommits.add(new CommitDto(gitLabApi, projectId, c));
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
}
