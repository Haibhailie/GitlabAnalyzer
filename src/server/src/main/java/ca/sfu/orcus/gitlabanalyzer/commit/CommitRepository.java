package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommitRepository {
    private final String defaultBranch = "master";

    // Overloaded method - no date required
    public ArrayList<CommitDTO> getAllCommits(GitLabApi gitLabApi, int projectID) throws GitLabApiException {
        ArrayList<CommitDTO> allCommits = new ArrayList<>();
        List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectID);

        return getAllCommitDTOS(gitLabApi, projectID, allCommits, allGitCommits);
    }

    // Overloaded method - date required
    public ArrayList<CommitDTO> getAllCommits(GitLabApi gitLabApi, int projectID, Date since, Date until) throws GitLabApiException {
        ArrayList<CommitDTO> allCommits = new ArrayList<>();
        List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectID, defaultBranch, since, until);

        return getAllCommitDTOS(gitLabApi, projectID, allCommits, allGitCommits);
    }

    private ArrayList<CommitDTO> getAllCommitDTOS(GitLabApi gitLabApi, int projectID, ArrayList<CommitDTO> allCommits, List<Commit> allGitCommits) throws GitLabApiException {
        for(Commit c : allGitCommits) {
            CommitDTO presentCommit = new CommitDTO(gitLabApi, projectID, c);
            allCommits.add(presentCommit);
        }
        return allCommits;
    }

    private CommitDTO getSingleCommitDTO(GitLabApi gitLabApi, int projectID, String sha) throws GitLabApiException {
        Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectID, sha);
        CommitDTO commitDTO = new CommitDTO(gitLabApi, projectID, gitCommit);
        return commitDTO;
    }
}
