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

    public List<NoteDto> getAllMergeRequestNotes(String jwt, int projectId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
    }

    public List<NoteDto> getAllIssuesNotes(String jwt, int projectId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
    }

    public List<NoteDto> getNotesByMemberId(String jwt, int projectId, int memberId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        try {
            List<MergeRequest> allMergeRequests = gitLabApi.getMergeRequestApi().getMergeRequests(projectId);
            List<Issue> allIssues = (List<Issue>) gitLabApi.getIssuesApi().getIssues(projectId);
            List<NoteDto> filteredNotes = new ArrayList<>();
            List<Note> allNotes = new ArrayList<>();
            for (MergeRequest mr : allMergeRequests) {
                allNotes.addAll(gitLabApi.getNotesApi().getMergeRequestNotes(projectId, mr.getIid()));
            }
            for (Issue issue : allIssues) {
                allNotes.addAll(gitLabApi.getNotesApi().getIssueNotes(projectId, issue.getIid()));
            }
            for (Note n: allNotes) {
                if(n.getAuthor().getId() == memberId) {
                    filteredNotes.add(new NoteDto(n));
                }
            }
            return filteredNotes;
        } catch (GitLabApiException e) {
            return null;
        }
    }

}
