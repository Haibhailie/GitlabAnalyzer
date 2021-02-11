package ca.sfu.orcus.gitlabanalyzer.mergeRequest;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.gitlab4j.api.utils.ISO8601;

import java.util.ArrayList;
import java.util.List;


public class MergeRequestRepository {

    static class MergeRequests{

        boolean hasConflicts = false;
        boolean isOpen = false;

        String assignedTo;
        String author;
        String description;
        String sourceBranch;
        String targetBranch;

        List<String> notesName;
        List<String> notes;


    }

    public void getAllMergeRequests(GitLabApi gitLabApi, int projectID) throws GitLabApiException {

        ArrayList<MergeRequests> listMR= new ArrayList<MergeRequests>();
        List<MergeRequest> mergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectID);
        String presentProjectName = gitLabApi.getProjectApi().getProject(projectID).getName();

        System.out.println("\n\nThe present open Merge Requests in " + presentProjectName + " are:");
        for (MergeRequest mr : mergeRequests) {
            int mrIndex = 0;
            MergeRequests presentMergeRequest = new MergeRequests();

            if (mr.getState().compareTo("opened") == 0) {
                presentMergeRequest.isOpen=true;

                //System.out.println(mr.getAuthor().getName() + " opened an MR to merge " + mr.getSourceBranch() + " with " + mr.getTargetBranch());
                presentMergeRequest.author = mr.getAuthor().getName();
                presentMergeRequest.sourceBranch = mr.getSourceBranch();
                presentMergeRequest.targetBranch = mr.getTargetBranch();

                //System.out.println("It is assigned to " + mr.getAssignee().getName());
                presentMergeRequest.assignedTo = mr.getAssignee().getName();


                System.out.println("Description: \n" + mr.getDescription());
                presentMergeRequest.description = mr.getDescription();

                if (mr.getHasConflicts()) {
                    //System.out.println("This MR has conflicts");
                    presentMergeRequest.hasConflicts = true;
                }
                else {
                    //System.out.println("This MR does not have any conflicts.");
                    presentMergeRequest.hasConflicts = false;
                }
                //System.out.println("\nRecent Activity:");
                List<Note> mrNotes = gitLabApi.getNotesApi().getMergeRequestNotes(projectID, mr.getIid());
                if (!mrNotes.isEmpty())
                    for (Note note : mrNotes) {
                        //System.out.println(note.getAuthor().getName() + ": \n" + note.getBody() + "\n");
                        presentMergeRequest.notesName.add(note.getAuthor().getName());
                        presentMergeRequest.notes.add(note.getBody());
                    }
            }
        }

    }
}


