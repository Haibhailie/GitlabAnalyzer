package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommitService {
    private final static String defaultBranch = "master";
    private final CommitRepository commitRepository;
    private final AuthenticationService authService;

    @Autowired
    public CommitService(CommitRepository commitRepository, AuthenticationService authService) {
        this.commitRepository = commitRepository;
        this.authService = authService;
    }

    public ArrayList<CommitDTO> getAllCommits(String jwt, int projectID, Date since, Date until) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            return getAllCommitDTOs(gitLabApi, projectID, since, until);
        } else {
            return null;
        }
    }

    private ArrayList<CommitDTO> getAllCommitDTOs(GitLabApi gitLabApi, int projectID, Date since, Date until) {
        try {
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectID, defaultBranch, since, until);
            ArrayList<CommitDTO> allCommits = new ArrayList<>();
            for(Commit commit : allGitCommits) {
                CommitDTO presentCommit = new CommitDTO(gitLabApi, projectID, commit);
                allCommits.add(presentCommit);
            }
            return allCommits;
        } catch(GitLabApiException e) {
            return null;
        }
    }

    public CommitDTO getSingleCommit(String jwt, int projectID, String sha) throws GitLabApiException {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectID, sha);
            return new CommitDTO(gitLabApi, projectID, gitCommit);
        } else {
            return null;
        }
    }

    public List<Diff> getDiffOfCommit(String jwt, int projectID, String sha) throws GitLabApiException {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            return gitLabApi.getCommitsApi().getDiff(projectID, sha);
        } else {
            return null;
        }
    }

}
