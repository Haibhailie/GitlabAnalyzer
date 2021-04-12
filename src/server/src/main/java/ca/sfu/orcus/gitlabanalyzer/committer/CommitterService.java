package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestRepository;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
import org.gitlab4j.api.GitLabApiException;
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
                                     Map<String, Integer> committerToMemberMap)throws GitLabApiException, NotFoundException {
        int memberId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isPresent() && userHasAccessToProject(memberId, projectId, projectUrl.get())) {
            updateCommitterMemberResolution(projectUrl.get(), committerToMemberMap);
            committerRepo.updateCommitters(projectId, committerToMemberMap);
        } else {
            throw new NotFoundException("Project not found for user");
        }
    }

    private boolean userHasAccessToProject(int memberId, int projectId, String projectUrl) {
        // TODO: use gitlab to check this
        return true;
    }

    private void updateCommitterMemberResolution(String projectUrl, Map<String, Integer> committerToMemberMap) {
        /*
         * 1. Get projectUrl (CHECK)
         * 2. Get the committerDtoDbs for all committers (keys of the map)
         * 3. For each committer involved
         *    - update the MemberDto field
         *    - for each of the commitIds
         *      - update the memberId field of the commit
         * 4. Update committerEmails in MemberDto
         * 5. Update isSolo field in MR
         */

        for (Map.Entry<String, Integer> entry : committerToMemberMap.entrySet()) {
            String committerEmail = entry.getKey();
            Integer memberId = entry.getValue();

            // Update CommitterDto.member
            MemberDtoDb memberDto = memberRepo.getMember(projectUrl, memberId).orElseThrow();
            projectRepo.updateCommittersMemberDto(projectUrl, committerEmail, memberDto);

            // Update Commit.userId for all commits by the committer
            Set<String> commitIds = projectRepo.getCommitIdsForCommitter(projectUrl, committerEmail);
            for (String commitId : commitIds) {
                mergeRequestRepo.updateCommitUserId(projectUrl, commitId, memberId);
            }

            // Update MemberDto.committerEmails
            memberDto.getCommitterEmails().add(committerEmail);
            memberRepo.cacheMember(memberDto, projectUrl);

            // Update MergeRequestDto.isSolo for all the MRs involved
        }
    }

    /*
    private void updateCommittersMemberDto(String projectUrl, String committerEmail, Integer memberId) {
        MemberDtoDb memberDto = memberRepo.getMember(projectUrl, memberId).orElseThrow();
        projectRepo.updateCommittersMemberDto(projectUrl, committerEmail, memberDto);
    }
    */
}
