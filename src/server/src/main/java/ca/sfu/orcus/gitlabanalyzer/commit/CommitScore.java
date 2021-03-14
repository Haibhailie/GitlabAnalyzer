package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.utils.DiffStringParser;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommitScore {
    private final double addLOCFactor = 1;
    private final double deleteLOCFactor = 0.2;
    private final double syntaxChangeFactor = 0.2;
    private final double blankLOCFactor = 0;
    private final double spacingChangeFactor = 0;

    int numLineAdditions = 0;
    int numLineDeletions = 0;
    int numBlankAdditions = 0;
    int numSyntaxChanges = 0;
    int numSpacingChanges = 0;

    List<String> generatedDiffList = new ArrayList<>();

    public double getCommitScore(GitLabApi gitLabApi, int projectId, String sha) throws GitLabApiException {
        List<Diff> commitDiffs = gitLabApi.getCommitsApi().getDiff(projectId, sha);

        //regex to split lines by new line and store in generatedDiffList
        generatedDiffList = Arrays.asList(DiffStringParser.parseDiff(commitDiffs).split("\\r?\\n"));

        parseDiffList();

        double totalScore = (numLineAdditions * addLOCFactor)
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
                System.out.println("Skipped line: " + lineNumber);
            } else if (line.startsWith("+++")) {
                System.out.println("Skipped line " + lineNumber);
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
            if (!line.startsWith("-")) {
                if (StringUtils.difference(testingLine, line).length() > (testingLine.length()) / 2) {
                    numSyntaxChanges++;
                    generatedDiffList.set(presentLine, "---");
                    break;
                }
            }
        }
    }
}

