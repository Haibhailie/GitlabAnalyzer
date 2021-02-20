package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
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
    private final AuthenticationService authService;

    @Autowired
    public MergeRequestService(MergeRequestRepository mergeRequestRepository, AuthenticationService authService) {
        this.mergeRequestRepository = mergeRequestRepository;
        this.authService = authService;
    }

    public List<MergeRequestDto> getAllMergeRequests(String jwt, int projectID, Date since, Date until){
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            List<MergeRequestDto> filteredMergeRequests = new ArrayList<>();
            try {
                List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
                for (MergeRequest mr : allMergeRequests) {
                    MergeRequestDto presentMergeRequest = new MergeRequestDto(gitLabApi, projectID, mr);
                    if (mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until))
                        filteredMergeRequests.add(presentMergeRequest);
                }
                return filteredMergeRequests;
            }
            catch (GitLabApiException g){
                return null;
            }
        } else {
            return null;
        }
    }

    public List<CommitDto> getAllCommitsFromMergeRequest(String jwt, int projectID, int mergeRequestID) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if ((gitLabApi != null)) {
            List<CommitDto> filteredCommits = new ArrayList<>();
            try {
                List<Commit> allCommits = gitLabApi.getMergeRequestApi().getCommits(projectID, mergeRequestID);
                for (Commit c : allCommits) {
                    filteredCommits.add(new CommitDto(gitLabApi, projectID, c));
                }
                return filteredCommits;
            } catch (GitLabApiException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<MergeRequestDiffDto> getDiffFromMergeRequest(String jwt, int projectID, int mergeRequestID){
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if ((gitLabApi != null)) {
            List<MergeRequestDiffDto> listDiff = new ArrayList<>();
            try {
                List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
                for (MergeRequest mr : mergeRequests) {
                    if (mr.getIid() == mergeRequestID) {
                        List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectID, mr.getIid());
                        for (Commit c : presentCommit) {
                            List<Diff> commitDiffs = gitLabApi.getCommitsApi().getDiff(projectID, c.getShortId());
                            for (Diff d : commitDiffs) {
                                listDiff.add(new MergeRequestDiffDto(c, d));
                            }
                        }
                    }
                }
                return listDiff;
            }
            catch (GitLabApiException g){
                return null;
            }
        } else {
            return null;
        }
    }
}
