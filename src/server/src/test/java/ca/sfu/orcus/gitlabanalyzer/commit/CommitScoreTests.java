package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import org.gitlab4j.api.models.Diff;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommitScoreTests {
    // Class to be tested
    private CommitScoreCalculator commitScore = new CommitScoreCalculator();

    @Test
    public void getScore() {
        List<Diff> diffList = CommitMock.createTestDiffList();

        double expectedScore = commitScore.getCommitScore(diffList);
        double actualScore = 3.8;

        assertEquals(expectedScore, actualScore);
    }

}
