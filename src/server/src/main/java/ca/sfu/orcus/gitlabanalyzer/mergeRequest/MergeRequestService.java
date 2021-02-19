package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
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

    public List<MergeRequestDTO> getAllMergeRequests(String jwt, int projectID, Date since, Date until) throws GitLabApiException {

        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            List<MergeRequestDTO> listMR = new ArrayList<>();
            List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
            for (MergeRequest mr : mergeRequests) {
                MergeRequestDTO presentMergeRequest = new MergeRequestDTO(gitLabApi, projectID, mr);
                if (mr.getCreatedAt().after(since) && mr.getCreatedAt().before(until))
                    listMR.add(presentMergeRequest);
            }
            return listMR;
        } else {
            return null;
        }
    }

    public List<CommitDTO> getAllCommitsFromMergeRequest(String jwt, int projectID, int mergeRequestID) throws GitLabApiException {

        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if ((gitLabApi != null)){
            List<CommitDTO> listCommit = new ArrayList<>();
            List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
            List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectID, mergeRequestID);
            for (Commit c : presentCommit) {
                CommitDTO tempDTO = new CommitDTO(gitLabApi, projectID, c);
                listCommit.add(tempDTO);
            }
            return listCommit;
        }
        else{
            return null;
        }
    }

    public List<MergeRequestDiffDTO> getDiffFromMergeRequest(String jwt, int projectID, int mergeRequestID) throws GitLabApiException {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if ((gitLabApi != null)) {
            List<MergeRequestDiffDTO> listDiff = new ArrayList<>();
            List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
            for (MergeRequest mr : mergeRequests) {
                List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectID, mr.getIid());
                for (Commit c : presentCommit) {
                    List<Diff> commitDiffs = gitLabApi.getCommitsApi().getDiff(projectID, c.getShortId());
                    for (Diff d : commitDiffs) {
                        listDiff.add(new MergeRequestDiffDTO(c, d));
                    }
                }
            }
            return listDiff;
        }
        else{
            return null;
        }
    }

}
