
package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectMock;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommitServiceTests {
    @Mock
    private GitLabApiWrapper gitLabApiWrapper;
    @Mock
    private Commit commit;

    // Class to be tested
    @InjectMocks
    private CommitService commitService;

    private GitLabApi gitLabApi = GitLabApiMock.getGitLabApiMock();
    private final CommitsApi commitsApi = gitLabApi.getCommitsApi();
    private static final String jwt = UUID.randomUUID().toString();

    // Test objects
    private static final int projectId = CommitMock.defaultId;
    private static final Date since = CommitMock.defaultDate;
    private static final Date until = CommitMock.defaultDate;

    private static Project project;

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
        project = ProjectMock.createProject();
    }

    void initialTestSetup() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        commit = CommitMock.createCommit();
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
    }
    // Testing the null checks

    @Test
    void getSingleCommitWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getSingleCommit(jwt, projectId, CommitMock.defaultSha));
    }

    @Test
    void getCommitsWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getAllCommits(jwt, projectId, since, until));
    }

    @Test
    void getSingleCommitDiffWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getDiffOfCommit(jwt, projectId, CommitMock.defaultSha));
    }

    // Testing the CommitService methods

    @Test
    public void getSingleCommit() throws GitLabApiException {
        initialTestSetup();

        when(commitsApi.getCommit(projectId, CommitMock.defaultSha)).thenReturn(commit);
        CommitDto commitDto = commitService.getSingleCommit(jwt, projectId, CommitMock.defaultSha);
        CommitDto expectedCommitDto = new CommitDto(gitLabApi, projectId, commit);

        assertEquals(commitDto, expectedCommitDto);
    }

    @Test
    public void getCommits() throws GitLabApiException {
        initialTestSetup();
        List<Commit> commitList = CommitMock.createTestCommitList();

        when(commitsApi.getCommits(projectId, ProjectMock.defaultDefaultBranch, since, until)).thenReturn(commitList);
        when(gitLabApi.getProjectApi().getProject(projectId)).thenReturn(project);
        when(commitsApi.getCommit(projectId, CommitMock.defaultSha)).thenReturn(commit);

        List<CommitDto> commitDtos = commitService.getAllCommits(jwt, projectId, since, until);
        List<CommitDto> expectedCommitDtos = new ArrayList<>();
        for (Commit c : commitList) {
            expectedCommitDtos.add(new CommitDto(gitLabApi, projectId, c));
        }
        assertEquals(commitDtos, expectedCommitDtos);
    }

    @Test
    public void getCommitsEmptyList() throws GitLabApiException {
        initialTestSetup();
        List<Commit> commitList = new ArrayList<>();

        when(commitsApi.getCommits(projectId, ProjectMock.defaultDefaultBranch, since, until)).thenReturn(commitList);
        when(gitLabApi.getProjectApi().getProject(projectId)).thenReturn(project);

        List<CommitDto> commitDtos = commitService.getAllCommits(jwt, projectId, since, until);
        List<CommitDto> expectedCommitDtos = new ArrayList<>();

        assertEquals(commitDtos, expectedCommitDtos);
    }

    @Test
    public void testGetSingleCommitDiff() throws GitLabApiException {
        initialTestSetup();

        String expectedDiffList = CommitMock.createTestDiffListString();
        when(commitsApi.getCommit(projectId, CommitMock.defaultSha)).thenReturn(commit);
        when(gitLabApi.getCommitsApi().getDiff(projectId, commit.getId())).thenReturn(CommitMock.createTestDiffList());
        String diffList = commitService.getDiffOfCommit(jwt, projectId, CommitMock.defaultSha);
        assertEquals(expectedDiffList, diffList);
    }

    // Testing the exception throws

    @Test
    public void getSingleCommitException() throws GitLabApiException {
        initialTestSetup();
        when(commitsApi.getCommit(projectId, CommitMock.defaultSha)).thenThrow(GitLabApiException.class);
        assertNull(commitService.getSingleCommit(jwt, projectId, CommitMock.defaultSha));
    }

    @Test
    public void getAllCommitsException() throws GitLabApiException {
        initialTestSetup();
        when(gitLabApi.getProjectApi().getProject(projectId)).thenReturn(project);
        when(commitsApi.getCommits(projectId, ProjectMock.defaultDefaultBranch, since, until)).thenThrow(GitLabApiException.class);
        assertNull(commitService.getAllCommits(jwt, projectId, since, until));
    }
}