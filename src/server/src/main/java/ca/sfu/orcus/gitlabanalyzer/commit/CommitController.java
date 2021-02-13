package ca.sfu.orcus.gitlabanalyzer.commit;


import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommitController {
    private final GitLabApi gitLabApi = null;

    @GetMapping("/api/core/{projectId}/commits")
    public List<CommitDTO> getCommits(@PathVariable(value="projectId") int projectId) throws GitLabApiException {
        return CommitRepository.getAllCommits(gitLabApi, projectId);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}")
    public CommitDTO getSingleCommit(@PathVariable(value="projectId") int projectId, @PathVariable(value="sha") String sha) throws GitLabApiException {
        return CommitRepository.getSingleCommit(gitLabApi, projectId, sha);
    }

    @GetMapping("/api/core/{projectId}/commit/{sha}/diff")
    public List<Diff> getSingleCommitDiffs(@PathVariable(value="projectId") int projectId, @PathVariable(value="sha") String sha) throws GitLabApiException {
        return CommitRepository.getDiffOfCommit(gitLabApi, projectId, sha);
    }



}
