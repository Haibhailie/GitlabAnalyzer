package ca.sfu.orcus.gitlabanalyzer.mocks;

import org.gitlab4j.api.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GitLabApiMock {
    private GitLabApiMock() {
        throw new AssertionError();
    }

    public static GitLabApi getGitLabApiMock() {
        GitLabApi gitLabApi = mock(GitLabApi.class);

        // Mock all required GitLabApi APIs
        CommitsApi commitsApi = mock(CommitsApi.class);
        MergeRequestApi mergeRequestApi = mock(MergeRequestApi.class);
        NotesApi notesApi = mock(NotesApi.class);
        ProjectApi projectApi = mock(ProjectApi.class);
        RepositoryApi repositoryApi = mock(RepositoryApi.class);
        UserApi userApi = mock(UserApi.class);

        // Mock GitLabApi's getApi() methods to return our mocks
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(gitLabApi.getUserApi()).thenReturn(userApi);

        return gitLabApi;
    }
}
