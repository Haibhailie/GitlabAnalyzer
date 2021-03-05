package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            return getAllMergeRequests(gitLabApi, projectId, since, until);
        }
    }

    private List<MergeRequestDto> getAllMergeRequests(GitLabApi gitLabApi, int projectId, Date since, Date until) {
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
        } catch (GitLabApiException g) {
            return null;
        }
    }

    public List<MergeRequestDto> getAllMergeRequests(GitLabApi gitLabApi, int projectId, Date since, Date until, int memberId) {
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
        } catch (GitLabApiException g) {
            return null;
        }
    }

    public List<CommitDto> getAllCommitsFromMergeRequest(String jwt, int projectId, int mergeRequestID) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if ((gitLabApi != null)) {
            List<CommitDto> filteredCommits = new ArrayList<>();
            try {
                List<Commit> allCommits = gitLabApi.getMergeRequestApi().getCommits(projectId, mergeRequestID);
                for (Commit c : allCommits) {
                    filteredCommits.add(new CommitDto(gitLabApi, projectId, c));
                }
                return filteredCommits;
            } catch (GitLabApiException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<MergeRequestDiffDto> getDiffFromMergeRequest(String jwt, int projectId, int mergeRequestID) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if ((gitLabApi != null)) {
            List<MergeRequestDiffDto> listDiff = new ArrayList<>();
            try {
                List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
                for (MergeRequest mr : mergeRequests) {
                    if (mr.getIid() == mergeRequestID) {
                        List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectId, mr.getIid());
                        for (Commit c : presentCommit) {
                            List<Diff> commitDiffs = gitLabApi.getCommitsApi().getDiff(projectId, c.getShortId());
                            for (Diff d : commitDiffs) {
                                listDiff.add(new MergeRequestDiffDto(c, d));
                            }
                        }
                    }
                }
                return listDiff;
            } catch (GitLabApiException g) {
                return null;
            }
        } else {
            return null;
        }
    }
}
