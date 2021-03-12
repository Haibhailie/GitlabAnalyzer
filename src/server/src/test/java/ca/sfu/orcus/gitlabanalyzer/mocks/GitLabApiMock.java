package ca.sfu.orcus.gitlabanalyzer.mocks;

import org.gitlab4j.api.*;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

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
        lenient().when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        lenient().when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        lenient().when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        lenient().when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        lenient().when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        lenient().when(gitLabApi.getUserApi()).thenReturn(userApi);

        return gitLabApi;
    }
}
