package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Arrays;
import java.util.List;

public class MergeRequestScoreCalculator {

    //TODO: Get these from config

    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public double getMergeRequestScore(MergeRequest mergeRequestChanges) {

        //regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);
        DiffScoreDto mergeRequestScoreDto = calculateScore(diffsList);

        return (mergeRequestScoreDto.getNumLineAdditions() * addLOCFactor)
                + (mergeRequestScoreDto.getNumLineDeletions() * deleteLOCFactor)
                + (mergeRequestScoreDto.getNumBlankAdditions() * blankLOCFactor)
                + (mergeRequestScoreDto.getNumSyntaxChanges() * syntaxChangeFactor)
                + (mergeRequestScoreDto.getNumSpacingChanges() * spacingChangeFactor);
    }

    private DiffScoreDto calculateScore(List<String> diffList) {
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.parseDiffList(diffList);
    }

}
