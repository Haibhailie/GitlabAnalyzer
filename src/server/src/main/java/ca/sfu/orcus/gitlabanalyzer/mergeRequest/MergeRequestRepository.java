package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.List;


public class MergeRequestRepository {


    public ArrayList<MergeRequestDTO> getAllMergeRequests(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MergeRequestDTO> listMR = new ArrayList<>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        String presentProjectName = gitLabApi.getProjectApi().getProject(projectID).getName();

        System.out.println("\n\nThe present open Merge Requests in " + presentProjectName + " are:");
        for (MergeRequest mr : mergeRequests) {
            MergeRequestDTO presentMergeRequest = new MergeRequestDTO(gitLabApi, projectID, mr);
            listMR.add(presentMergeRequest);
        }
        return listMR;
    }

}


