package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.utils.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.DiffScoreDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DiffStringParser;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

public class CommitScore {
    // Should be getting these from config
    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public double getCommitScore(List<Diff> diffs) {
        List<String> diffList = Arrays.asList(DiffStringParser.parseDiff(diffs).split("\\r?\\n"));
        DiffScoreDto commitScoreDto = calculateScore(diffList);

        double totalScore = (commitScoreDto.getNumLineAdditions() * addLOCFactor)
                + (commitScoreDto.getNumLineDeletions() * deleteLOCFactor)
                + (commitScoreDto.getNumBlankAdditions() * blankLOCFactor)
                + (commitScoreDto.getNumSyntaxChanges() * syntaxChangeFactor)
                + (commitScoreDto.getNumSpacingChanges() * spacingChangeFactor);

        return totalScore;
    }

    private DiffScoreDto calculateScore(List<String> diffList) {
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.parseDiffList(diffList);
    }
}
