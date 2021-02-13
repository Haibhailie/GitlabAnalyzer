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


public class CommitService {
    private double addLOCFactor = 1;
    private double deleteLOCFactor = 0.2;
    private double syntaxChangeFactor = 0.2;
    private double blankLOCFactor = 0;
    private double SpacingChangeFactor = 0;

    private final List<String> commentTypes = Arrays.asList("//", "/*");

    double getCommitScore(GitLabApi gitLabApi, int projectId, String sha) throws GitLabApiException {
        double score = 0;
        Commit commit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
        List<Diff> diffs = gitLabApi.getCommitsApi().getDiff(projectId, sha);
        for(Diff d : diffs) {
            score += getAddLOCScore(d);
            score += getSyntaxChangeScore(d);
//            score += getBlankLOCScore(commit, d);
        }
        score += getDeleteLOCScore(commit);
        return score;
    }

    double getAddLOCScore(Diff diff) {
        // Removing blank lines
        String s = diff.getDiff().replace("+\n", "");
        long LOCCount = s.chars().filter(ch -> ch == '\n').count() - 1; // -1 due to a header line in each diff
        return LOCCount * addLOCFactor;
    }

    double getDeleteLOCScore(Commit commit) {
        return commit.getStats().getDeletions() * deleteLOCFactor;
    }

    double getSyntaxChangeScore(Diff diff) {
        return 0 * syntaxChangeFactor;
    }

    double getBlankLOCScore(Commit commit, Diff diff) {
        // Removing blank lines
        String s = diff.getDiff().replace("+\n", "");
        long LOCCount = s.chars().filter(ch -> ch == '\n').count() - 1; // -1 due to a header line in each diff
        long blankLOCCount = commit.getStats().getAdditions() - LOCCount;

        long commentsCount = freqOfComment(diff);

        return (blankLOCCount + commentsCount) * blankLOCFactor;
    }

    // https://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
    private static long freqOfComment(Diff diff) {
        int lastIndex = 0;
        int count = 0;
        String comment = getFileType(diff);
        if(comment.equals("")) {
            return 0;
        }

        while(lastIndex != -1){
            lastIndex = diff.getDiff().indexOf(comment,lastIndex);
            if(lastIndex != -1){
                count ++;
                lastIndex = diff.getDiff().substring(lastIndex).indexOf("\n", lastIndex);
            }
        }
        return count;
    }

    private static String getFileType(Diff diff) {
        int indexOfPeriod = diff.getNewPath().indexOf('.');
        String fileType = diff.getNewPath().substring(indexOfPeriod);
        if(fileType.equals(".java") || fileType.equals(".cpp")) {
            return "//";
        } else if(fileType.equals(".js")) {
            return "/*";
        } else {
            return "";
        }
    }

}
