package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DiffScoreCalculator {

    private int numLineAdditions = 0;
    private int numLineDeletions = 0;
    private int numBlankAdditions = 0;
    private int numSyntaxChanges = 0;
    private int numSpacingChanges = 0;
    private final double lineLengthFactor = 0.5;
    private List<String> generatedDiffList = new ArrayList<>();

    public DiffScoreDto parseDiffList(List<String> diffStrings) {
        generatedDiffList = diffStrings;
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
        return new DiffScoreDto(numLineAdditions, numLineDeletions, numBlankAdditions, numSyntaxChanges, numSpacingChanges);
    }

    private boolean checkSyntaxChanges(int lineNumber, String testingLine) {
        int presentLine = 0;
        for (String line : generatedDiffList) {
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking the level of similarity between the two lines (if difference > half the original line, then it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, line).length() > (testingLine.length()) * lineLengthFactor) {
                    numSyntaxChanges++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
            presentLine++;
        }
        return false;
    }

    private boolean checkAddedBlankSpaces(int lineNumber, String testingLine) {
        int presentLine = 0;
        for (String line : generatedDiffList) {
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
            presentLine++;
        }
        return false;
    }
}
