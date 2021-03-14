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

        totalScore += (numLineAdditions * addLOCFactor)
                + (numLineDeletions * deleteLOCFactor)
                + (numBlankAdditions * blankLOCFactor)
                + (numSyntaxChanges * syntaxChangeFactor)
                + (numSpacingChanges * spacingChangeFactor);

        System.out.println(totalScore);
        return totalScore;
    }

    private void parseDiffList() {
        int lineNumber = -1;
        for (String line : generatedDiffList) {
            lineNumber++;
            if (line.startsWith("---")) {
                //Log line skipped
            } else if (line.startsWith("+++")) {
                //Log line skipped
            } else if (line.startsWith("+")) {

                if (line.substring(1).length() > 0) {
                    numLineAdditions++;
                } else {
                    numBlankAdditions++;
                }
            } else if (line.startsWith("-")) {
                if (checkSyntaxChanges(lineNumber, line)) {
                    break;
                }
                if (checkAddedBlankSpaces(lineNumber, line)) {
                    break;
                } else {
                    numLineDeletions++;
                }
            }
        }
    }

    private boolean checkSyntaxChanges(int lineNumber, String testingLine) {
        int presentLine = -1;
        for (String line : generatedDiffList) {
            presentLine++;
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking the level of similarity between the two lines (if difference > half the original line, then it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, line).length() > (testingLine.length()) / 2) {
                    numSyntaxChanges++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkAddedBlankSpaces(int lineNumber, String testingLine) {
        int presentLine = -1;
        for (String line : generatedDiffList) {
            presentLine++;
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking whether all the differences between two lines are just blank spaces
                if (StringUtils.difference(testingLine, line).isBlank()) {
                    numBlankAdditions++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
        }
        return false;
    }
}
