package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.DiffStringParser;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeRequestScore {

    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    int numLineAdditions = 0;
    int numLineDeletions = 0;
    int numBlankAdditions = 0;
    int numSyntaxChanges = 0;
    int numSpacingChanges = 0;

    List<String> generatedDiffList = new ArrayList<>();

    public double getMergeRequestScore(GitLabApi gitLabApi, int projectId, int mergeRequestId) throws GitLabApiException {
        double totalScore = 0;
        MergeRequest mergeRequestChanges = gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId);

        //regex to split lines by new line and store in generatedDiffList
        generatedDiffList = Arrays.asList(DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n"));

        parseDiffList();

        totalScore += (numLineAdditions * addLOCFactor) +
                (numLineDeletions * deleteLOCFactor) +
                (numBlankAdditions * blankLOCFactor) +
                (numSyntaxChanges * syntaxChangeFactor) +
                (numSpacingChanges * spacingChangeFactor);

        System.out.println(totalScore);
        return totalScore;
    }

    private void parseDiffList() {
        int lineNumber = -1;
        for (String line : generatedDiffList) {
            lineNumber++;
            if (line.startsWith("---")) {
                System.out.println("Skipped line: "+lineNumber);
            } else if (line.startsWith("+++")) {
                System.out.println("Skipped line "+lineNumber);
            } else if (line.startsWith("+")) {

                if (line.substring(1).length() > 0) {
                    numLineAdditions++;
                } else {
                    numBlankAdditions++;
                }
            } else if (line.startsWith("-")) {
                numLineDeletions++;
                checkSyntaxChanges(lineNumber, line);
            }
        }
    }

    private void checkSyntaxChanges(int lineNumber, String testingLine) {
        int presentLine = -1;
        for (String line : generatedDiffList) {
            presentLine++;
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                if (StringUtils.difference(testingLine, line).length() > (testingLine.length()) / 2) {
                    numSyntaxChanges++;
                    generatedDiffList.set(presentLine, "+++");
                    break;
                }
            }
        }

    }

}
