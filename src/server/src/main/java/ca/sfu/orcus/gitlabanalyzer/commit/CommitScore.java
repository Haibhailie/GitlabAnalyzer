package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

//Adding a LOC: +1
//Comments/blank lines: +0
//Deleting a LOC: +0.2
//Spacing change: +0
//Syntax change: +0.2

public class CommitScore {
    private final double addLOCFactor = 1;
    private final double deleteLOCFactor = 0.2;
    private final double syntaxChangeFactor = 0.2;
    private final double blankLOCFactor = 0;
    private final double spacingChangeFactor = 0;

    private static final List<String> commentDoubleSlash = Arrays.asList(".java", ".cpp", ".txt"); // added .txt for test purposes
    private static final List<String> commentSlashStar = Arrays.asList(".js", ".css");

    public double getCommitScore(GitLabApi gitLabApi, int projectId, String sha) throws GitLabApiException {
        double score = 0;
        Commit commit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
        List<Diff> diffs = gitLabApi.getCommitsApi().getDiff(projectId, sha);
        for (Diff d : diffs) {
            score += getAddLOCScore(d);
            score += getSyntaxChangeScore(d); // currently 0
            score += getBlankLOCScore(commit, d);
            score += getSpacingChangeScore(d);

        }
        score += getDeleteLOCScore(commit);
        return score;
    }

    double getAddLOCScore(Diff diff) {
        long actualLOCCount = getLinesOfActualCode(diff);
        return actualLOCCount * addLOCFactor;
    }

    double getDeleteLOCScore(Commit commit) {
        return commit.getStats().getDeletions() * deleteLOCFactor;
    }

    double getSyntaxChangeScore(Diff diff) {
        return 0 * syntaxChangeFactor;
    }

    double getBlankLOCScore(Commit commit, Diff diff) {
        long actualLOCCount = getLinesOfActualCode(diff);
        long blankLOCCount = commit.getStats().getAdditions() - actualLOCCount;
        long commentsCount = getNumComments(diff);

        return (blankLOCCount + commentsCount) * blankLOCFactor;
    }

    private double getSpacingChangeScore(Diff d) {
        return 0 * spacingChangeFactor;
    }

    long getLinesOfActualCode(Diff diff) {
        // Remove blank lines
        String str = diff.getDiff().replace("+\n", "");

        String strFind = "\n+";
        int count = 0;
        int fromIndex = 0;

        while ((fromIndex = str.indexOf(strFind, fromIndex)) != -1 ) {
            count++;
            fromIndex++;
        }
        return count;
    }

    // https://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
    private static long getNumComments(Diff diff) {
        int lastIndex = 0;
        int count = 0;
        String comment = getFileType(diff);
        if (comment.equals("")) {
            return 0;
        }
        while (lastIndex != -1) {
            lastIndex = diff.getDiff().indexOf(comment, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex = diff.getDiff().substring(lastIndex).indexOf("\n", lastIndex);
            }
        }
        return count;
    }

    private static String getFileType(Diff diff) {
        int indexOfPeriod = diff.getNewPath().indexOf('.');
        String fileType = diff.getNewPath().substring(indexOfPeriod);
        if (commentDoubleSlash.contains(fileType)) {
            return "//";
        } else if (commentSlashStar.contains(fileType)) {
            return "/*";
        } else {
            return "";
        }
    }
}

