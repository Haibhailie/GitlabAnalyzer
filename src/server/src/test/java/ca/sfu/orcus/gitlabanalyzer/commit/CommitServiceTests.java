package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommitServiceTests {
    @Mock private CommitRepository commitRepository;
    @Mock private AuthenticationService authenticationService;
    @Mock private GitLabApi gitLabApi;
    @Mock private CommitsApi commitsApi;
    @Mock private RepositoryApi repositoryApi;

    // Class to be tested
    @InjectMocks
    private CommitService commitService;

    // Test objects
    private static Commit commit;
    private static CommitStats commitStats;

    private static final String jwt = "";
    private static final int projectId = 5;
    private static final int count = 10;
    private static final String sha = "d7d1b7b568e50f99c93216404fa7187564c1738a";
    private static final Date since = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_SINCE));
    private static final Date until = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_UNTIL));

    @BeforeAll
    public static void setup() {
        commitStats = getTestCommitStats();
        commit = getTestCommit();
    }

    @Test
    void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        assertNull(commitService.getSingleCommit(jwt, projectId, sha));
        assertNull(commitService.getAllCommits(jwt, projectId, since, until));
    }

    @Test
    public void getSingleCommitTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commit);

        CommitDto commitDto = commitService.getSingleCommit(jwt, projectId, sha);
        CommitDto expectedCommitDto = new CommitDto(gitLabApi, projectId, commit);

        assertNotNull(commitDto);
        assertEquals(commitDto, expectedCommitDto);
    }

    public static Commit getTestCommit() {
        Commit commit = new Commit();

        commit.setId(String.valueOf(projectId));
        commit.setId(sha);
        commit.setCommittedDate(until);
        commit.setStats(commitStats);
        commit.setShortId(sha.substring(0, 10));

        return commit;
    }

    public static CommitStats getTestCommitStats() {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(count);
        commitStats.setDeletions(count);
        commitStats.setTotal(count*2);

        return commitStats;
    }


}
