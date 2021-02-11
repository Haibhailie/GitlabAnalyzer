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

    double getCommitScore(GitLabApi gitAccount, int projectId, String sha) throws GitLabApiException {
        double score = 0;
        Commit commit = gitAccount.getCommitsApi().getCommit(projectId, sha);
        List<Diff> diffs = gitAccount.getCommitsApi().getDiff(projectId, sha);

        return score;
    }

    double getAddLOCScore(Commit commit, Diff diffs) {
        long LOCCount = diffs.getDiff().chars().filter(ch -> ch == '\n').count();
        return commit.getStats().getAdditions() * addLOCFactor;
    }

    double getDeleteLOCScore(Commit commit) {
        return commit.getStats().getDeletions() * deleteLOCFactor;
    }

    double getSyntaxChangeScore(Commit commit) {
        return 0;
    }

}
