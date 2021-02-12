package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

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

    double getCommitScore(GitLabApi gitLabApi, int projectId, String sha) throws GitLabApiException {
        double score = 0;
        Commit commit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
        List<Diff> diffs = gitLabApi.getCommitsApi().getDiff(projectId, sha);
        for(Diff d : diffs) {
            score += getAddLOCScore(commit, d);
            score += getDeleteLOCScore(commit);
            score += getSyntaxChangeScore(commit);
        }
        return score;
    }

    double getAddLOCScore(Commit commit, Diff diff) {
        // Removing blank lines
        String s = diff.getDiff().replace("+\n", "");
        long LOCCount = s.chars().filter(ch -> ch == '\n').count() - 1; // -1 due to a header line in each diff
        return LOCCount * addLOCFactor;
    }

    double getDeleteLOCScore(Commit commit) {
        return commit.getStats().getDeletions() * deleteLOCFactor;
    }

    double getSyntaxChangeScore(Commit commit) {
        return 0;
    }

}
