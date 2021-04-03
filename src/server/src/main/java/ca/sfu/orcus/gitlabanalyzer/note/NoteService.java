package ca.sfu.orcus.gitlabanalyzer.note;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.springframework.data.util.Pair;
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
import java.util.Map;

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
        Map<String, Pair<Integer, List<Note>>> allNotes = getAllNotes(gitLabApi, projectId);

        for (String webUrl : allNotes.keySet()) {
            for (Note n : allNotes.get(webUrl).getSecond()) {
                if (n.getAuthor().getId() == memberId && !n.getSystem()) {
                    try {
                        String by = allNotes.get(webUrl).getFirst() == memberId ? "self" : gitLabApi.getProjectApi().getMember(projectId, memberId).getName();
                        filteredNotes.add(new NoteDto(n, webUrl, by));
                    } catch (GitLabApiException e) {
                        filteredNotes.add(new NoteDto(n, webUrl, ""));
                    }
                }
            }
        }
        return filteredNotes;
    }

    private Map<String, Pair<Integer, List<Note>>> getAllNotes(GitLabApi gitLabApi, int projectId) {
        Map<String, Pair<Integer, List<Note>>> allNotes = new HashMap<>();
        allNotes.putAll(getAllMergeRequestsNotes(gitLabApi, projectId));
        allNotes.putAll(getAllIssuesNotes(gitLabApi, projectId));
        return allNotes;
    }

    private Map<String, Pair<Integer, List<Note>>> getAllMergeRequestsNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId);
            Map<String, Pair<Integer, List<Note>>> allMergeRequestsNotes = new HashMap<>();
            for (MergeRequest mr : allMergeRequests) {
                allMergeRequestsNotes.put(mr.getWebUrl(),
                    Pair.of(mr.getAuthor().getId(), gitLabApi.getNotesApi().getMergeRequestNotes(projectId, mr.getIid())));
            }
            return allMergeRequestsNotes;
        } catch (GitLabApiException e) {
            return new HashMap<>();
        }
    }

    private Map<String, Pair<Integer, List<Note>>> getAllIssuesNotes(GitLabApi gitLabApi, int projectId) {
        try {
            List<Issue> allIssues = gitLabApi.getIssuesApi().getIssues(Integer.valueOf(projectId));
            Map<String, Pair<Integer, List<Note>>> allIssuesNotes = new HashMap<>();
            for (Issue issue : allIssues) {
                allIssuesNotes.put(issue.getWebUrl(),
                    Pair.of(issue.getAuthor().getId(), gitLabApi.getNotesApi().getIssueNotes(projectId, issue.getIid())));
            }
            return allIssuesNotes;
        } catch (GitLabApiException e) {
            return new HashMap<>();
        }
    }

}
