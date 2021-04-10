package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
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

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<MergeRequestDto> getAllMergeRequests(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        } else {
            return returnAllMergeRequests(gitLabApi, projectId, since, until);
        }
    }

    public List<MergeRequestDto> getMergeRequestsByMemberId(String jwt, int projectId, Date since, Date until, int memberId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return returnAllMergeRequests(gitLabApi, projectId, since, until, memberId);
    }

    private List<MergeRequestDto> returnAllMergeRequests(GitLabApi gitLabApi, int projectId, Date since, Date until) {
        try {
            List<MergeRequestDto> filteredMergeRequests = new ArrayList<>();
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
            for (MergeRequest mr : allMergeRequests) {
                if (mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until)) {
                    MergeRequestDto presentMergeRequest = new MergeRequestDto(gitLabApi, projectId, mr);
                    filteredMergeRequests.add(presentMergeRequest);
                }
            }
            return filteredMergeRequests;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private List<MergeRequestDto> returnAllMergeRequests(GitLabApi gitLabApi, int projectId, Date since, Date until, int memberId) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            List<MergeRequestDto> filteredMergeRequests = new ArrayList<>();
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
            for (MergeRequest mr : allMergeRequests) {
                if (mr.getAuthor().getId() == memberId && mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until)) {
                    MergeRequestDto presentMergeRequest = new MergeRequestDto(gitLabApi, projectId, mr);
                    filteredMergeRequests.add(presentMergeRequest);
                }
            }
            return filteredMergeRequests;
        } catch (GitLabApiException e) {
            return null;
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
