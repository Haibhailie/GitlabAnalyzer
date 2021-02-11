package ca.sfu.orcus.gitlabanalyzer.mergeRequest;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.List;


public class MergeRequestRepository {


    public ArrayList<MergeRequestDTO> getAllMergeRequests(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MergeRequestDTO> listMR= new ArrayList<>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        String presentProjectName = gitLabApi.getProjectApi().getProject(projectID).getName();

        System.out.println("\n\nThe present open Merge Requests in " + presentProjectName + " are:");
        for (MergeRequest mr : mergeRequests) {

            MergeRequestDTO presentMergeRequest = new MergeRequestDTO();

            presentMergeRequest.setOpen(mr.getState().compareTo("opened") == 0);

            presentMergeRequest.setAuthor(mr.getAuthor().getName());

            presentMergeRequest.setSourceBranch(mr.getSourceBranch());

            presentMergeRequest.setTargetBranch(mr.getTargetBranch());

            presentMergeRequest.setAssignedTo(mr.getAssignee().getName());

            presentMergeRequest.setDescription(mr.getDescription());

            presentMergeRequest.setHasConflicts(mr.getHasConflicts());

            presentMergeRequest.setCommits(gitLabApi.getMergeRequestApi().getCommits(projectID, mr.getIid()));

            presentMergeRequest.setParticipants(gitLabApi.getMergeRequestApi().getParticipants(projectID, mr.getIid()));

            ArrayList<String> notesName = new ArrayList<>();
            ArrayList<String> notes = new ArrayList<>();
            List<Note> mrNotes = gitLabApi.getNotesApi().getMergeRequestNotes(projectID, mr.getIid());
            if (!mrNotes.isEmpty()) {
                for (Note note : mrNotes) {
                    notesName.add(note.getAuthor().getName());
                    notes.add(note.getBody());
                }
                presentMergeRequest.setNotes(notes);
                presentMergeRequest.setNotesName(notesName);
            }
            listMR.add(presentMergeRequest);
        }
        return listMR;
    }

}


