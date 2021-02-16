package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.Project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommitService {
    private final static String defaultBranch = "master";

    public static ArrayList<CommitDTO> getAllCommits(GitLabApi gitLabApi, int projectID, Date since, Date until) throws GitLabApiException {
        List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectID, defaultBranch, since, until, gitLabApi.getProjectApi().getProject(projectID).getPath(), true, true, false);

        return getAllCommitDTOS(gitLabApi, projectID, allGitCommits);
    }

    private static ArrayList<CommitDTO> getAllCommitDTOS(GitLabApi gitLabApi, int projectID, List<Commit> allGitCommits) throws GitLabApiException {
        ArrayList<CommitDTO> allCommits = new ArrayList<>();
        for(Commit commit : allGitCommits) {
            CommitDTO presentCommit = new CommitDTO(gitLabApi, projectID, commit);
            allCommits.add(presentCommit);
        }
        return allCommits;
    }

    public static CommitDTO getSingleCommit(GitLabApi gitLabApi, int projectID, String sha) throws GitLabApiException {
        Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectID, sha);
        return new CommitDTO(gitLabApi, projectID, gitCommit);
    }

    public static List<Diff> getDiffOfCommit(GitLabApi gitLabApi, int projectID, String sha) throws GitLabApiException {
        return gitLabApi.getCommitsApi().getDiff(projectID, sha);
    }

}
