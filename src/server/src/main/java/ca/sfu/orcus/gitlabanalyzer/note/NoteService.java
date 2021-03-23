package ca.sfu.orcus.gitlabanalyzer.note;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public NoteService(NoteRepository noteRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.noteRepository = noteRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<NoteDto> getNoteDtosByMemberId(String jwt, int projectId, int memberId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return returnAllNotesDtosByMemberId(gitLabApi, projectId, memberId);
    }

    private List<NoteDto> returnAllNotesDtosByMemberId(GitLabApi gitLabApi, int projectId, int memberId) {
        List<NoteDto> filteredNotes = new ArrayList<>();
        List<Note> allNotes = getAllNotes(gitLabApi, projectId);

        for (Note n : allNotes) {
            if (n.getAuthor().getId() == memberId) {
                filteredNotes.add(new NoteDto(n));
            }
        }

        return filteredNotes;
    }

    private List<Note> getAllNotes(GitLabApi gitLabApi, int projectId) {
        List<Note> allNotes = new ArrayList<>();
        allNotes.addAll(getAllMergeRequestsNotes(gitLabApi, projectId));
        allNotes.addAll(getAllIssuesNotes(gitLabApi, projectId));
        return allNotes;
    }

    private List<Note> getAllMergeRequestsNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId);
            List<Note> allMergeRequestsNotes = new ArrayList<>();
            for (MergeRequest mr : allMergeRequests) {
                allMergeRequestsNotes.addAll(gitLabApi.getNotesApi().getMergeRequestNotes(projectId, mr.getIid()));
            }
            return allMergeRequestsNotes;
        } catch (GitLabApiException e) {
            return new ArrayList<>();
        }
    }

    private List<Note> getAllIssuesNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<Issue> allIssues = gitLabApi.getIssuesApi().getIssues();
            List<Issue> allIssuesByProjectId = new ArrayList<>();
            for (Issue issue : allIssues) {
                if (issue.getProjectId() == projectId) {
                    allIssuesByProjectId.add(issue);
                }
            }
            List<Note> allIssuesNotes = new ArrayList<>();
            for (Issue issue : allIssuesByProjectId) {
                allIssuesNotes.addAll(gitLabApi.getNotesApi().getIssueNotes(projectId, issue.getIid()));
            }
            return allIssuesNotes;
        } catch (GitLabApiException e) {
            return new ArrayList<>();
        }
    }

}
