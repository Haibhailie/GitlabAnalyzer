package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Note;
import org.gitlab4j.api.models.Participant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeRequestDto {
    private int mergeRequestId;
    private String title;
    private boolean hasConflicts;
    private boolean isOpen;
    private int userId;
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

    public MergeRequestDto(GitLabApi gitLabApi, int projectId, MergeRequest presentMergeRequest) throws GitLabApiException {
        setMergeRequestId(presentMergeRequest.getIid());
        setMergeRequestTitle(presentMergeRequest.getTitle());
        setOpen(presentMergeRequest.getState().compareTo("opened") == 0);
        setAuthor(presentMergeRequest.getAuthor().getName());
        setUserId(presentMergeRequest.getAuthor().getId());
        setSourceBranch(presentMergeRequest.getSourceBranch());
        setTargetBranch(presentMergeRequest.getTargetBranch());
        if (presentMergeRequest.getAssignee() == null) {
            setAssignedTo("Unassigned");
        } else {
            setAssignedTo(presentMergeRequest.getAssignee().getName());
        }
        setDescription(presentMergeRequest.getDescription());
        setHasConflicts(presentMergeRequest.getHasConflicts());
        setTime(presentMergeRequest.getMergedAt().getTime());

        setCommitters(gitLabApi.getMergeRequestApi().getCommits(projectId, presentMergeRequest.getIid()));
        setNumAdditions(gitLabApi.getMergeRequestApi().getCommits(projectId, presentMergeRequest.getIid()), gitLabApi, projectId);
        setNumDeletions(gitLabApi.getMergeRequestApi().getCommits(projectId, presentMergeRequest.getIid()), gitLabApi, projectId);
        setParticipants(gitLabApi.getMergeRequestApi().getParticipants(projectId, presentMergeRequest.getIid()));

        ArrayList<String> notesName = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        List<Note> mrNotes = gitLabApi.getNotesApi().getMergeRequestNotes(projectId, presentMergeRequest.getIid());
        if (!mrNotes.isEmpty()) {
            for (Note note : mrNotes) {
                notesName.add(note.getAuthor().getName());
                notes.add(note.getBody());
            }
            setNotes(notes);
            setNotesName(notesName);
        }
    }

    public void setMergeRequestId(int mergeRequestID) {
        this.mergeRequestId = mergeRequestID;
    }

    public void setMergeRequestTitle(String title) {
        this.title = title;
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
        Set<String> commitAuthorsSet = new HashSet<>();
        for (Commit c : commits) {
            commitAuthorsSet.add(c.getAuthorName());
        }

        committers = new ArrayList<>(commitAuthorsSet);
    }

    public void setUserId(int userID) {
        this.userId = userID;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MergeRequestDto)) {
            return false;
        }

        MergeRequestDto m = (MergeRequestDto) o;

        return (this.mergeRequestId == (m.mergeRequestId)
                && this.hasConflicts == (m.hasConflicts)
                && this.isOpen == (m.isOpen)
                && this.assignedTo.equals(m.assignedTo)
                && this.author.equals(m.author)
                && this.description.equals(m.description)
                && this.sourceBranch.equals(m.sourceBranch)
                && this.targetBranch.equals(m.targetBranch)
                && this.numAdditions == (m.numAdditions)
                && this.numDeletions == (m.numDeletions)
                && this.committers.equals(m.committers)
                && this.participants.equals(m.participants)
                && this.time == (m.time));

    }
}
