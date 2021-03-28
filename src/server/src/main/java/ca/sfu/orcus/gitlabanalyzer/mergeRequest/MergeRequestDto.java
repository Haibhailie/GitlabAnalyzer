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
    private List<String> notesName;
    private List<String> notes;
    private List<String> committers;
    private List<Participant> participants;
    private long time;
    private double score;
    private String webUrl;

    public MergeRequestDto(GitLabApi gitLabApi, int projectId, MergeRequest presentMergeRequest) throws GitLabApiException {
        int mergeRequestId = presentMergeRequest.getIid();

        setMergeRequestId(mergeRequestId);
        setMergeRequestTitle(presentMergeRequest.getTitle());
        setHasConflicts(presentMergeRequest.getHasConflicts());
        setOpen(presentMergeRequest.getState().toLowerCase().compareTo("opened") == 0);
        setUserId(presentMergeRequest.getAuthor().getId());

        if (presentMergeRequest.getAssignee() == null) {
            setAssignedTo("Unassigned");
        } else {
            setAssignedTo(presentMergeRequest.getAssignee().getName());
        }

        setAuthor(presentMergeRequest.getAuthor().getName());
        setDescription(presentMergeRequest.getDescription());
        setSourceBranch(presentMergeRequest.getSourceBranch());
        setTargetBranch(presentMergeRequest.getTargetBranch());

        List<Commit> commits = gitLabApi.getMergeRequestApi().getCommits(projectId, mergeRequestId);
        setCommitters(commits);
        setNumAdditionsAndDeletions(commits, gitLabApi, projectId);

        setParticipants(gitLabApi.getMergeRequestApi().getParticipants(projectId, mergeRequestId));
        setNotesNameAndNotes(gitLabApi, projectId, mergeRequestId);
        setTime(presentMergeRequest.getMergedAt().getTime());
        MergeRequestScoreCalculator scoreCalculator = new MergeRequestScoreCalculator();
        setScore(scoreCalculator.getMergeRequestScore(gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId)));
        setWebUrl(presentMergeRequest.getWebUrl());
    }

    public void setMergeRequestId(int mergeRequestId) {
        this.mergeRequestId = mergeRequestId;
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

    public void setUserId(int userId) {
        this.userId = userId;
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

    public void setNumAdditionsAndDeletions(List<Commit> commits, GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        numAdditions = 0;
        numDeletions = 0;

        for (Commit c : commits) {
            Commit presentCommit = gitLabApi.getCommitsApi().getCommit(projectId, c.getShortId());
            if (presentCommit.getStats() != null) {
                numAdditions += presentCommit.getStats().getAdditions();
                numDeletions += presentCommit.getStats().getDeletions();
            }
        }
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setCommitters(List<Commit> commits) {
        Set<String> commitAuthorsSet = new HashSet<>();
        for (Commit c : commits) {
            commitAuthorsSet.add(c.getAuthorName());
        }

        committers = new ArrayList<>(commitAuthorsSet);
    }

    public void setNotesNameAndNotes(GitLabApi gitLabApi, int projectId, int mergeRequestId) throws GitLabApiException {
        List<String> notesName = new ArrayList<>();
        List<String> notes = new ArrayList<>();

        List<Note> mrNotes = gitLabApi.getNotesApi().getMergeRequestNotes(projectId, mergeRequestId);
        for (Note n : mrNotes) {
            notesName.add(n.getAuthor().getName());
            notes.add(n.getBody());
        }

        this.notesName = notesName;
        this.notes = notes;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
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
                && this.title.equals(m.title)
                && this.hasConflicts == (m.hasConflicts)
                && this.isOpen == (m.isOpen)
                && this.userId == (m.userId)
                && this.assignedTo.equals(m.assignedTo)
                && this.author.equals(m.author)
                && this.description.equals(m.description)
                && this.sourceBranch.equals(m.sourceBranch)
                && this.targetBranch.equals(m.targetBranch)
                && this.numAdditions == (m.numAdditions)
                && this.numDeletions == (m.numDeletions)
                && this.notesName.equals(m.notesName)
                && this.notes.equals(m.notes)
                && this.committers.equals(m.committers)
                && this.participants.equals(m.participants)
                && this.time == (m.time)
                && this.score == (m.score));
    }
}
