package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.ScoreDto;
import org.gitlab4j.api.models.MergeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeRequestScoreCalculator {

    //TODO: Get these from config

    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public List<ScoreDto> getMergeRequestScore(MergeRequest mergeRequestChanges) {

        List<ScoreDto> scoreDtos = new ArrayList<>();
        List<DiffScoreDto> diffScoreDtos = new ArrayList<>();
        //regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);

        //Loop that separates the diffs and scores of individual files in a Merge Request diff
        for (int i = 0; i < diffsList.size(); i++) {
            if (diffsList.get(i).startsWith("diff --")) {
                for (int j = i + 1; j < diffsList.size(); j++) {
                    if (diffsList.get(j).startsWith("diff --")) {
                        scoreDtos.add(new ScoreDto(convertToString(diffsList.subList(i, j - 1))));
                        diffScoreDtos.add(calculateScore(diffsList.subList(i, j - 1)));
                    }
                }
            }
        }

        for (int i = 0; i < diffScoreDtos.size(); i++) {
            double totalScore = (diffScoreDtos.get(i).getNumLineAdditions() * addLOCFactor)
                    + (diffScoreDtos.get(i).getNumLineDeletions() * deleteLOCFactor)
                    + (diffScoreDtos.get(i).getNumBlankAdditions() * blankLOCFactor)
                    + (diffScoreDtos.get(i).getNumSyntaxChanges() * syntaxChangeFactor)
                    + (diffScoreDtos.get(i).getNumSpacingChanges() * spacingChangeFactor);

            scoreDtos.get(i).setScores(totalScore,
                    diffScoreDtos.get(i).getNumLineAdditions(),
                    diffScoreDtos.get(i).getNumLineDeletions(),
                    diffScoreDtos.get(i).getNumBlankAdditions(),
                    diffScoreDtos.get(i).getNumSyntaxChanges(),
                    diffScoreDtos.get(i).getNumSpacingChanges());
        }

        return scoreDtos;
    }

    private DiffScoreDto calculateScore(List<String> diffList) {
        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.parseDiffList(diffList);
    }

    private String[] convertToString(List<String> stringList) {
        Object[] objectList = stringList.toArray();
        String[] stringArray = Arrays.copyOf(objectList, objectList.length, String[].class);
        return stringArray;
    }
}
