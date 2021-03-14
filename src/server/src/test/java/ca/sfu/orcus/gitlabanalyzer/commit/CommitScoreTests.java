package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommitScoreTests {

    // Class to be tested
    @InjectMocks
    private CommitScore commitScore;

    private GitLabApi gitLabApi;

    // Test objects
    private static final int projectId = CommitMock.defaultId;

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
    }

    // Testing CommitScore

    @Test
    public void getScore() throws GitLabApiException {
        List<Diff> diffList = CommitMock.createTestDiffList();

        when(gitLabApi.getCommitsApi().getDiff(projectId, CommitMock.defaultSha)).thenReturn(diffList);

        double expectedScore = commitScore.getCommitScore(gitLabApi, projectId, CommitMock.defaultSha);
        double actualScore = 1.6;

        assertEquals(expectedScore, actualScore);
    }
}
