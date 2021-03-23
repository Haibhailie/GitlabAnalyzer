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
import java.util.HashMap;
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
        HashMap<String, List<Note>> allNotes = getAllNotes(gitLabApi, projectId);

        for (String webUrl : allNotes.keySet()) {
            for (Note n : allNotes.get(webUrl)) {
                if (n.getAuthor().getId() == memberId && n.getSystem() == false) {
                    filteredNotes.add(new NoteDto(n, webUrl));
                }
            }
        }
        return filteredNotes;
    }

    private HashMap<String, List<Note>> getAllNotes(GitLabApi gitLabApi, int projectId) {
        HashMap<String, List<Note>> allNotes = new HashMap<>();
        allNotes.putAll(getAllMergeRequestsNotes(gitLabApi, projectId));
        allNotes.putAll(getAllIssuesNotes(gitLabApi, projectId));
        return allNotes;
    }

    private HashMap<String, List<Note>> getAllMergeRequestsNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId);
            HashMap<String, List<Note>> allMergeRequestsNotes = new HashMap<>();
            for (MergeRequest mr : allMergeRequests) {
                allMergeRequestsNotes.put(mr.getWebUrl(), gitLabApi.getNotesApi().getMergeRequestNotes(projectId, mr.getIid()));
            }
            return allMergeRequestsNotes;
        } catch (GitLabApiException e) {
            return new HashMap<>();
        }
    }

    private HashMap<String, List<Note>> getAllIssuesNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<Issue> allIssues = gitLabApi.getIssuesApi().getIssues(Integer.valueOf(projectId));

            HashMap<String, List<Note>> allIssuesNotes = new HashMap<>();
            for (Issue issue : allIssues) {
                allIssuesNotes.put(issue.getWebUrl(), gitLabApi.getNotesApi().getIssueNotes(projectId, issue.getIid()));
            }
            return allIssuesNotes;
        } catch (GitLabApiException e) {
            return new HashMap<>();
        }
    }

}
