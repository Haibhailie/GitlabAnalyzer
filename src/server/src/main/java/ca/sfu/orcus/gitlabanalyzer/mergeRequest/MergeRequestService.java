package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDTO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.MergeRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergeRequestService {

    public static List<MergeRequestDTO> getAllMergeRequests(GitLabApi gitLabApi, int projectID, Date since, Date until) throws GitLabApiException {

        List<MergeRequestDTO> listMR = new ArrayList<>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        for (MergeRequest mr : mergeRequests) {
            MergeRequestDTO presentMergeRequest = new MergeRequestDTO(gitLabApi, projectID, mr);
            if(mr.getCreatedAt().after(since)&&mr.getCreatedAt().before(until))
                listMR.add(presentMergeRequest);
        }
        return listMR;
    }

    public static List<CommitDTO> getAllCommitsFromMergeRequest(GitLabApi gitLabApi, int projectID, int mergeRequestID) throws GitLabApiException {

        List<CommitDTO> listCommit = new ArrayList<>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectID, mergeRequestID);
        for (Commit c : presentCommit) {
            CommitDTO tempDTO = new CommitDTO(gitLabApi, projectID, c);
            listCommit.add(tempDTO);
        }
        return listCommit;
    }

    public static List<MergeRequestDiffDTO> getDiffFromMergeRequest(GitLabApi gitLabApi, int projectID, int mergeRequestID) throws GitLabApiException {
        List<MergeRequestDiffDTO> listDiff = new ArrayList<>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        for (MergeRequest mr : mergeRequests) {
            List<Commit> presentCommit = gitLabApi.getMergeRequestApi().getCommits(projectID, mr.getId());
            for(Commit c:presentCommit) {
                List<Diff> commitDiffs = gitLabApi.getCommitsApi().getDiff(projectID, c.getShortId());
                for(Diff d : commitDiffs) {
                    listDiff.add(new MergeRequestDiffDTO(c, d));
                }
            }
        }
        return listDiff;
    }


}
