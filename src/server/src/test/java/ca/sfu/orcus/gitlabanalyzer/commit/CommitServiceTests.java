package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommitServiceTests {
    @Mock private AuthenticationService authenticationService;
    @Mock private GitLabApi gitLabApi;
    @Mock private CommitsApi commitsApi;
    @Mock private ProjectApi projectApi;

    // Class to be tested
    @InjectMocks
    private CommitService commitService;

    // Test objects
    private static Commit commit;
    private static CommitStats commitStats;

    private static final String jwt = "";
    private static final int projectId = 5;
    private static final String title = "title";
    private static final String author = "Jimmy Jonathon Jacobs";
    private static final String authorEmail = "jjj@verizon.net";
    private static final String message = "";
    private static final int count = 10;
    private static final String sha = "abcd1234";
    private static final Date since = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_SINCE));
    private static final Date until = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_UNTIL));
    private static final String defaultBranch = "master";
    private static final List<Commit> commitList = new ArrayList<>();
    private static Project project;


    @BeforeAll
    public static void setup() {
        commitStats = getTestCommitStats();
        commit = getTestCommit();
        project = getTestProject();
    }

    @Test
    void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        assertNull(commitService.getSingleCommit(jwt, projectId, sha));
        assertNull(commitService.getAllCommits(jwt, projectId, since, until));
    }

    @Test
    public void testGetAllCommits() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(projectApi.getProject(projectId)).thenReturn(project);
        when(commitsApi.getCommits(projectId, defaultBranch, since, until)).thenReturn(commitList);

        List<CommitDto> commitDtos = commitService.getAllCommits(jwt, projectId, since, until);
        List<CommitDto> expectedCommitDtos = new ArrayList<>();

        assertNotNull(commitDtos);
        assertEquals(commitDtos, expectedCommitDtos);
    }

    @Test
    public void testGetSingleCommit() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commit);

        CommitDto commitDto = commitService.getSingleCommit(jwt, projectId, sha);
        CommitDto expectedCommitDto = new CommitDto(gitLabApi, projectId, commit);

        assertNotNull(commitDto);
        assertEquals(commitDto, expectedCommitDto);
    }

    @Test
    public void testGetSingleCommitDiff() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);

        List<Diff> commitDiff = commitService.getDiffOfCommit(jwt, projectId, sha);
        List<Diff> expectedCommitDiff = new ArrayList<>();

        assertNotNull(commitDiff);
        assertEquals(commitDiff, expectedCommitDiff);
    }

    public static Commit getTestCommit() {
        Commit commit = new Commit();

        commit.setId(String.valueOf(projectId));
        commit.setTitle(title);
        commit.setAuthorName(author);
        commit.setAuthorEmail(authorEmail);
        commit.setMessage(message);
        commit.setId(sha);
        commit.setCommittedDate(until);
        commit.setStats(commitStats);
        commit.setShortId(sha);

        return commit;
    }

    public static CommitStats getTestCommitStats() {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(count);
        commitStats.setDeletions(count);
        commitStats.setTotal(count*2);

        return commitStats;
    }

    private static Project getTestProject() {
        Project project = new Project();

        project.setDefaultBranch(defaultBranch);

        return project;
    }
}
