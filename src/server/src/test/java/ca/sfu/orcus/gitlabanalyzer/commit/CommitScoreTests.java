package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import org.gitlab4j.api.models.Diff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommitScoreTests {
    // Class to be tested
    @InjectMocks
    private CommitScoreCalculator commitScore;

    // Testing CommitScore

    @Test
    public void getScore() {
        List<Diff> diffList = CommitMock.createTestDiffList();

        double expectedScore = commitScore.getCommitScore(diffList);
        double actualScore = 3.8;

        assertEquals(expectedScore, actualScore);
    }

}
