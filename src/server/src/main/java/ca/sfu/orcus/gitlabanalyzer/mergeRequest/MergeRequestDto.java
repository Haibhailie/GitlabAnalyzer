package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Note;
import org.gitlab4j.api.models.Participant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergeRequestDto {

    private int mergeRequestID;
    private boolean hasConflicts;
    private boolean isOpen;
    private int userID;
    private String assignedTo;
    private String author;
    private String description;
    private String sourceBranch;
    private String targetBranch;
    private int numAdditions;
    private int numDeletions;
    private ArrayList<String> notesName;
    private ArrayList<String> notes;
    private ArrayList<String> committers;
    private List<Participant> participants;
    private long time;

    public MergeRequestDto(GitLabApi gitLabApi, int projectID, MergeRequest presentMergeRequest) throws GitLabApiException {
        setMergeRequestID(presentMergeRequest.getIid());
        setOpen(presentMergeRequest.getState().compareTo("opened") == 0);
        setAuthor(presentMergeRequest.getAuthor().getName());
        setUserID(presentMergeRequest.getAuthor().getId());
        setSourceBranch(presentMergeRequest.getSourceBranch());
        setTargetBranch(presentMergeRequest.getTargetBranch());
        if (presentMergeRequest.getAssignee() == null) {
            setAssignedTo("Unassigned");
        } else {
            setAssignedTo(presentMergeRequest.getAssignee().getName());
        }
        setDescription(presentMergeRequest.getDescription());
        setHasConflicts(presentMergeRequest.getHasConflicts());
        setCommitters(gitLabApi.getMergeRequestApi().getCommits(projectID, presentMergeRequest.getIid()));

        setNumAdditions(gitLabApi.getMergeRequestApi().getCommits(projectID, presentMergeRequest.getIid()), gitLabApi, projectID);
        setNumDeletions(gitLabApi.getMergeRequestApi().getCommits(projectID, presentMergeRequest.getIid()), gitLabApi, projectID);

        setTime(presentMergeRequest.getMergedAt().getTime());

        setParticipants(gitLabApi.getMergeRequestApi().getParticipants(projectID, presentMergeRequest.getIid()));
        ArrayList<String> notesName = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        List<Note> mrNotes = gitLabApi.getNotesApi().getMergeRequestNotes(projectID, presentMergeRequest.getIid());
        if (!mrNotes.isEmpty()) {
            for (Note note : mrNotes) {
                notesName.add(note.getAuthor().getName());
                notes.add(note.getBody());
            }
            setNotes(notes);
            setNotesName(notesName);
        }
    }

    public void setMergeRequestID(int mergeRequestID) {
        this.mergeRequestID = mergeRequestID;
    }

    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }

    public void setNotesName(ArrayList<String> notesName) {
        this.notesName = notesName;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setNumAdditions(List<Commit> commits, GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        numAdditions = 0;
        for (Commit c : commits) {
            Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectId, c.getShortId());
            if (presentCommit.getStats() != null) {
                numAdditions += presentCommit.getStats().getAdditions();
            }
        }
    }

    public void setNumDeletions(List<Commit> commits, GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        numDeletions = 0;
        for (Commit c : commits) {
            Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectId, c.getShortId());
            if (presentCommit.getStats() != null) {
                numDeletions += presentCommit.getStats().getDeletions();
            }
        }
    }

    public void setCommitters(List<Commit> commits) {
        committers = new ArrayList<>();
        for (Commit c : commits) {
            //Checks if committer is already present in list, prevent duplicate authors
            String commitAuthor = c.getAuthorName();
            boolean isPresent = false;
            for (String committer : committers) {
                if (committer.compareTo(commitAuthor) == 0) {
                    isPresent = true;
                    break;
                }
            }
            if (!isPresent) {
                committers.add(commitAuthor);
            }
        }
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setTime(long time) { this.time = time; }

    public int getUserID(){
        return userID;
    }


}
