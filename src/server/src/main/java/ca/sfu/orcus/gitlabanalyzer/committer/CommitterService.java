package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommitterService {
    private final CommitterRepository committerRepo;
    private final ProjectRepository projectRepo;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public CommitterService(@Qualifier("mockCommitterRepo") CommitterRepository committerRepo,
                            ProjectRepository projectRepo,
                            GitLabApiWrapper gitLabApiWrapper) {
        this.committerRepo = committerRepo;
        this.projectRepo = projectRepo;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public Optional<List<CommitterDto>> getCommittersInProject(String jwt, int projectId) throws GitLabApiException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent() && userHasAccessToProject(memberId, projectId, projectUrl.get())) {
            return committerRepo.getCommitterTableForProject(projectId);
        } else {
            return Optional.empty();
        }
    }

    public void updateCommitterTable(String jwt,
                                     int projectId,
                                     Map<String, Integer> committerToMemberMap) throws GitLabApiException, NotFoundException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent() && userHasAccessToProject(memberId, projectId, projectUrl.get())) {
            updateCommitterMemberResolution(projectId, committerToMemberMap);
            committerRepo.updateCommitters(projectId, committerToMemberMap);
        } else {
            throw new NotFoundException("Project not found for user");
        }
    }

    private boolean userHasAccessToProject(int memberId, int projectId, String projectUrl) {
        try {
            return projectRepo.projectIsPublic(projectId,
                    VariableDecoderUtil.decode("GITLAB_URL")) || projectRepo.containsMember(projectId, projectUrl, memberId);
        } catch (NotFoundException e) {
            return false;
        }
    }

    private void updateCommitterMemberResolution(int projectId, Map<String, Integer> committerToMemberMap) {
        /**
         * 1. Get projectUrl
         * 2. Get the committerDtoDbs for all committers (keys of the map)
         * 3. For each committer
         *    - update the MemberDto field
         *    - for each of the commitIds
         *      - update the memberId field of the commit
         */
        String projectUrl = null;
    }
}
