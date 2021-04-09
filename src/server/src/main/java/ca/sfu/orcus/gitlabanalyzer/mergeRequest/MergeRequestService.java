package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
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
    private final CommitService commitService;

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, GitLabApiWrapper gitLabApiWrapper, CommitService commitService) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.commitService = commitService;
    }

    public List<MergeRequestDto> getAllMergeRequests(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        } else {
            return returnAllMergeRequests(gitLabApi, projectId, since, until);
        }
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

    public List<MergeRequestDto> returnAllMergeRequests(GitLabApi gitLabApi, int projectId, Date since, Date until, int memberId) {
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

    public List<MergeRequestDto> getOrphanMergeRequestByMemberName(GitLabApi gitLabApi, int projectId, Date since, Date until, String memberName) {
        try {
            List<MergeRequestDto> orphanMergeRequestByMemberName = new ArrayList<>();
            List<CommitDto> allCommitsByMemberName = commitService.returnAllCommits(gitLabApi, projectId, since, until, memberName);
            Set<Integer> addedMergeRequests = new HashSet<>();
            for (CommitDto c : allCommitsByMemberName) {
                List<MergeRequest> relatedMergeRequests = gitLabApi.getCommitsApi().getMergeRequests(projectId, c.getId());
                for (MergeRequest mr : relatedMergeRequests) {
                    if (!memberName.equalsIgnoreCase(mr.getAuthor().getName()) && !addedMergeRequests.contains(mr.getIid())) {
                        addedMergeRequests.add(mr.getIid());
                        orphanMergeRequestByMemberName.add(new MergeRequestDto(gitLabApi, projectId, mr));
                    }
                }
            }
            return orphanMergeRequestByMemberName;
        } catch (GitLabApiException e) {
            return  null;
        }
    }

    public List<CommitDto> getOrphanCommitsFromOrphanMergeRequestByMemberName(GitLabApi gitLabApi, int projectId, int mergeRequestId, String memberName) {
        List<CommitDto> allCommits = returnAllCommitsFromMergeRequest(gitLabApi, projectId, mergeRequestId);
        List<CommitDto> orphanCommits = new ArrayList<>();
        for (CommitDto c : allCommits) {
            if (c.getAuthor().equalsIgnoreCase(memberName)) {
                orphanCommits.add(c);
            }
        }
        return orphanCommits;
    }
}
