package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitStatsMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectMock;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommitServiceTests {
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private CommitsApi commitsApi;

    // Class to be tested
    @InjectMocks
    private CommitService commitService;

    private GitLabApi gitLabApi;
    private static final String jwt = UUID.randomUUID().toString();

    // Test objects
    private static final int projectId = ProjectMock.defaultId;
    private static final Date since = CommitMock.defaultDate;
    private static final Date until = CommitMock.defaultDate;

    private static final String defaultBranch = "master";
    private static Project project;


    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
        project = new Project();
        project.setDefaultBranch(defaultBranch);
    }

    // Testing the null checks

    @Test
    void getSingleCommitWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getSingleCommit(jwt, ProjectMock.defaultId, CommitMock.defaultSha));
    }

    @Test
    void getCommitsWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getAllCommits(jwt, ProjectMock.defaultId, since, until));
    }

    @Test
    void getSingleCommitDiffWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(commitService.getDiffOfCommit(jwt, projectId, CommitMock.defaultSha));
    }

    // Testing the methods

    @Test
    public void getSingleCommit() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        CommitStats commitStats = CommitStatsMock.createCommitStats();
        Commit commit = CommitMock.createCommit(commitStats);

        when(gitLabApi.getCommitsApi().getCommit(projectId, CommitMock.defaultSha)).thenReturn(commit);

        CommitDto commitDto = commitService.getSingleCommit(jwt, projectId, CommitMock.defaultSha);
        CommitDto expectedCommitDto = new CommitDto(gitLabApi, projectId, commit);

        assertNotNull(commitDto);
        assertEquals(commitDto, expectedCommitDto);
    }

    @Test
    public void getCommits() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<Commit> commitList = CommitMock.createTestCommitList();
        CommitStats commitStats = CommitStatsMock.createCommitStats();
        Commit commit = CommitMock.createCommit(commitStats);

        when(gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until)).thenReturn(commitList);
        when(gitLabApi.getProjectApi().getProject(projectId)).thenReturn(project);
        when(gitLabApi.getCommitsApi().getCommit(projectId, CommitMock.defaultSha)).thenReturn(commit);

        List<CommitDto> commitDtos = commitService.getAllCommits(jwt, projectId, since, until);
        List<CommitDto> expectedCommitDtos = new ArrayList<>();
        for(Commit c : commitList) {
            expectedCommitDtos.add(new CommitDto(gitLabApi, projectId, c));
        }
        assertNotNull(commitDtos);
        assertEquals(commitDtos, expectedCommitDtos);
    }

    @Test
    public void testGetSingleCommitDiff() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<Diff> diffList = CommitMock.createTestDiffList();

        when(gitLabApi.getCommitsApi().getDiff(projectId, CommitMock.defaultSha)).thenReturn(diffList);

        List<Diff> commitDiff = commitService.getDiffOfCommit(jwt, projectId, CommitMock.defaultSha);
        List<Diff> expectedCommitDiff = new ArrayList<>(diffList);

        assertNotNull(commitDiff);
        assertEquals(commitDiff, expectedCommitDiff);
    }

    // Testing the exception throws

    @Test
    public void getSingleCommitException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, CommitMock.defaultSha)).thenThrow(GitLabApiException.class);
        assertNull(commitService.getSingleCommit(jwt, projectId, CommitMock.defaultSha));
    }

    @Test
    public void getAllCommitsException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getProjectApi().getProject(projectId)).thenReturn(project);
        when(commitsApi.getCommits(projectId, defaultBranch, since, until)).thenThrow(GitLabApiException.class);
        assertNull(commitService.getAllCommits(jwt, projectId, since, until));
    }

    @Test
    public void getSingleCommitDiffException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getDiff(projectId, CommitMock.defaultSha)).thenThrow(GitLabApiException.class);
        assertNull(commitService.getDiffOfCommit(jwt, projectId, CommitMock.defaultSha));
    }
}
