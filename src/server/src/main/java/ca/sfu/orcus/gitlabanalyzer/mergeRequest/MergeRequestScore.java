package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.DiffScoreDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DiffStringParser;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Arrays;
import java.util.List;

public class MergeRequestScore {

    //Should be getting these from config
    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public double getMergeRequestScore(MergeRequest mergeRequestChanges) {

        //regex to split lines by new line and store in generatedDiffList
        List<String> diffsList = Arrays.asList(DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n"));
        DiffScoreDto mergeRequestScoreDto = calculateScore(diffsList);

        double totalScore = (mergeRequestScoreDto.getNumLineAdditions() * addLOCFactor)
                + (mergeRequestScoreDto.getNumLineDeletions() * deleteLOCFactor)
                + (mergeRequestScoreDto.getNumBlankAdditions() * blankLOCFactor)
                + (mergeRequestScoreDto.getNumSyntaxChanges() * syntaxChangeFactor)
                + (mergeRequestScoreDto.getNumSpacingChanges() * spacingChangeFactor);

        System.out.println(totalScore);
        return totalScore;
    }

    private DiffScoreDto calculateScore(List<String> diffList){
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        DiffScoreDto mergeRequestScoreDto = diffScoreCalculator.parseDiffList(diffList);
        return mergeRequestScoreDto;
    }

}
