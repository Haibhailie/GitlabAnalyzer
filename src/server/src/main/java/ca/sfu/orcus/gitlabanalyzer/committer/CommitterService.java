package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitterDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestRepository;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
import org.bson.Document;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.*;

@Service
public class CommitterService {
    private final CommitterRepository committerRepo;
    private final MergeRequestRepository mergeRequestRepo;
    private final MemberRepository memberRepo;
    private final ProjectRepository projectRepo;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public CommitterService(@Qualifier("mockCommitterRepo") CommitterRepository committerRepo,
                            MergeRequestRepository mergeRequestRepo,
                            MemberRepository memberRepo,
                            ProjectRepository projectRepo,
                            GitLabApiWrapper gitLabApiWrapper) {
        this.committerRepo = committerRepo;
        this.mergeRequestRepo = mergeRequestRepo;
        this.memberRepo = memberRepo;
        this.projectRepo = projectRepo;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public Optional<List<CommitterDtoDb>> getCommittersInProject(String jwt, int projectId) throws GitLabApiException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent() && userHasAccessToProject(memberId, projectId, projectUrl.get())) {
            ProjectDtoDb project = projectRepo.getProject(projectUrl.get())
                    .orElseThrow(() -> new GitLabApiException("Could not get project document"));
            return Optional.of(project.getCommitters());
        } else {
            return Optional.empty();
        }
    }

    public void updateCommitterTable(String jwt,
                                     int projectId,
                                     Map<String, Integer> committerToMemberMap)
            throws GitLabApiException, NotFoundException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent() && userHasAccessToProject(memberId, projectId, projectUrl.get())) {
            updateCommitterMemberResolutionOrThrow(projectUrl.get(), committerToMemberMap);
            committerRepo.updateCommitters(projectId, committerToMemberMap);
        } else {
            throw new NotFoundException("Project not found for user");
        }
    }

    private boolean userHasAccessToProject(int memberId, int projectId, String projectUrl) {
        // TODO: use gitlab to check this
        return true;
    }

    private void updateCommitterMemberResolutionOrThrow(String projectUrl, Map<String, Integer> committerToMemberMap)
            throws NotFoundException {
        for (Map.Entry<String, Integer> entry : committerToMemberMap.entrySet()) {
            String committerEmail = entry.getKey();
            Integer memberId = entry.getValue();

            // Update CommitterDto.member
            MemberDtoDb memberDto = memberRepo.getMember(projectUrl, memberId)
                    .orElseThrow(() -> new NotFoundException("Could not fetch member"));
            projectRepo.updateCommittersMemberDto(projectUrl, committerEmail, memberDto);

            // Update Commit.userId for all commits by the committer
            CommitterDtoDb committerDto = projectRepo.getCommitter(projectUrl, committerEmail)
                    .orElseThrow(() -> new NotFoundException("Could not fetch committer"));
            for (String commitId : committerDto.getCommitIds()) {
                mergeRequestRepo.updateCommitUserId(projectUrl, commitId, memberId);
            }

            // Update MemberDto.committerEmails
            memberDto.getCommitterEmails().add(committerEmail);
            memberRepo.cacheMember(memberDto, projectUrl);

            // Update MergeRequestDto.isSolo for all the MRs involved
            for (Integer mrId : committerDto.getMergeRequestIds()) {
                boolean isSolo = true;
                List<String> committerEmails = mergeRequestRepo.getCommitterEmailsForMergeRequest(projectUrl, mrId);
                for (String c : committerEmails) {
                    if (!committerToMemberMap.getOrDefault(c, -1).equals(memberId)) {
                        isSolo = false;
                        break;
                    }
                }
                mergeRequestRepo.setSolo(projectUrl, mrId, isSolo);
            }
        }
    }
}
