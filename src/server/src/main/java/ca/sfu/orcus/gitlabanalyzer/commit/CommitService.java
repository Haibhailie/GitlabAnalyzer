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
    private final CommitRepository commitRepository;
    private final AuthenticationService authService;

    @Autowired
    public CommitService(CommitRepository commitRepository, AuthenticationService authService) {
        this.commitRepository = commitRepository;
        this.authService = authService;
    }

    public List<CommitDto> getAllCommits(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi == null) {
            return null;
        }
        return getAllCommitDtos(gitLabApi, projectId, since, until);
    }

    private List<CommitDto> getAllCommitDtos(GitLabApi gitLabApi, int projectId, Date since, Date until) {
        try {
            String defaultBranch = gitLabApi.getProjectApi().getProject(projectId).getDefaultBranch();
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until);
            List<CommitDto> allCommits = new ArrayList<>();
            for(Commit commit : allGitCommits) {
                CommitDto presentCommit = new CommitDto(gitLabApi, projectId, commit);
                allCommits.add(presentCommit);
            }
            return allCommits;
        } catch(GitLabApiException e) {
            return null;
        }
    }

    public CommitDto getSingleCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi == null) {
            return null;
        }
        try {
            Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
            return new CommitDto(gitLabApi, projectId, gitCommit);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public List<Diff> getDiffOfCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi == null) {
            return null;
        }
        try {
            return gitLabApi.getCommitsApi().getDiff(projectId, sha);
        } catch (GitLabApiException e) {
            return null;
        }
    }
}
