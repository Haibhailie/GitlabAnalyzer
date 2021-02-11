package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import java.util.ArrayList;
import java.util.List;

public class MergeRequestDTO {

    private boolean hasConflicts;
    private boolean isOpen;
    private String assignedTo;
    private String author;
    private String description;
    private String sourceBranch;
    private String targetBranch;
    private ArrayList<String> notesName;
    private ArrayList<String> notes;

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


}
