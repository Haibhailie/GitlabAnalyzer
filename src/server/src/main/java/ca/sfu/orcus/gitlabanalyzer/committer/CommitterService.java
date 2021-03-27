package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

@Service
public class CommitterService {
    private final CommitterRepository committerRepo;
    private final MemberRepository memberRepo;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public CommitterService(CommitterRepository committerRepo, MemberRepository memberRepo, GitLabApiWrapper gitLabApiWrapper) {
        this.committerRepo = committerRepo;
        this.memberRepo = memberRepo;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public Optional<List<CommitterDto>> getCommittersInProject(String jwt, int projectId) throws GitLabApiException, BadRequestException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        if (memberRepo.projectContainsMember(projectId, memberId)) {
            return committerRepo.getCommitterTableForProject(projectId);
        } else {
            throw new BadRequestException("Member does not have access to this project");
        }
    }
}
