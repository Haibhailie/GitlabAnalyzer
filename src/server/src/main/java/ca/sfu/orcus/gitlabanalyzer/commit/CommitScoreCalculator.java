package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.utils.diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.diff.DiffScoreDto;
import ca.sfu.orcus.gitlabanalyzer.utils.diff.DiffStringParser;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

public class CommitScoreCalculator {
    // TODO: Should be getting these from config
    private static final double addLocFactor = 1;
    private static final double deleteLocFactor = 0.2;
    private static final double syntaxChangeFactor = 0.2;
    private static final double blankLocFactor = 0;
    private static final double spacingChangeFactor = 0;

    public static double getCommitScore(List<Diff> diffs) {
        // regex to split lines by new line and store in generatedDiffList
        String[] diffArray = DiffStringParser.parseDiff(diffs).split("\\r?\\n");
        List<String> diffList = Arrays.asList(diffArray);
        DiffScoreDto commitScoreDto = generateDiffScoreDto(diffList);

        double totalScore = (commitScoreDto.getNumLineAdditions() * addLocFactor)
                + (commitScoreDto.getNumLineDeletions() * deleteLocFactor)
                + (commitScoreDto.getNumBlankAdditions() * blankLocFactor)
                + (commitScoreDto.getNumSyntaxChanges() * syntaxChangeFactor)
                + (commitScoreDto.getNumSpacingChanges() * spacingChangeFactor);

        return totalScore;
    }

    private static DiffScoreDto generateDiffScoreDto(List<String> diffList) {
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.parseDiffList(diffList);
    }
}
