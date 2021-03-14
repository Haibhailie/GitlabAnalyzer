package ca.sfu.orcus.gitlabanalyzer.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DiffScoreCalculator {

    int numLineAdditions = 0;
    int numLineDeletions = 0;
    int numBlankAdditions = 0;
    int numSyntaxChanges = 0;
    int numSpacingChanges = 0;
    List<String> generatedDiffList = new ArrayList<>();

    public DiffScoreDto parseDiffList(List<String> passedDiffString) {
        generatedDiffList = passedDiffString;
        int lineNumber = -1;
        for (String line : generatedDiffList) {
            lineNumber++;
            if (line.startsWith("---")) {
                // Log line skipped
            } else if (line.startsWith("+++")) {
                // Log line skipped
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
        return new DiffScoreDto(numLineAdditions, numLineDeletions, numBlankAdditions, numSyntaxChanges, numSpacingChanges);
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
                // Checking the level of similarity between the two lines (if difference > half the original line, then it's considered a new addition, else a syntax change)
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
                // Checking whether all the differences between two lines are just blank spaces
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
