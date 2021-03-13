package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.DiffStringParser;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;

import java.io.IOException;
import java.util.ArrayList;
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
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        parseDiffList(diffString);
        totalScore += (numLineAdditions * addLOCFactor) +
                (numLineDeletions * deleteLOCFactor) +
                (numBlankAdditions * blankLOCFactor) +
                (numSyntaxChanges * syntaxChangeFactor) +
                (numSpacingChanges * spacingChangeFactor);
        System.out.println(totalScore);
        return totalScore;
    }

    private void parseDiffList(String[] diffsList) {
        for (String line : diffsList) {
            if (line.startsWith("---")) {
                continue;
            }
            else if (line.startsWith("+++")) {
                continue;
            }
            else if (line.startsWith("+")) {

                if (line.substring(1).length() > 0) {
                    numLineAdditions++;
                } else {
                    numBlankAdditions++;
                }
            } else if (line.startsWith("-")) {
                numLineDeletions++;

            }
        }
    }
}
