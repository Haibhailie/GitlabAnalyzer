package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("cached")
public class MemberServiceCached implements MemberService {
    GitLabApiWrapper gitLabApiWrapper;
    ProjectRepository projectRepo;
    MemberRepository memberRepo;

    @Autowired
    MemberServiceCached(GitLabApiWrapper gitLabApiWrapper, MemberRepository memberRepo, ProjectRepository projectRepo) {
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.projectRepo = projectRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    public List<MemberDto> getAllMembers(String jwt, int projectId) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isEmpty()) {
            return null;
        }
        List<String> memberDocumentIds = projectRepo.getMemberDocIds(projectId, projectUrl.get());
        return memberRepo.getMembers(memberDocumentIds);
    }

    @Override
    public List<CommitDto> getCommitsByMemberName(String jwt, int projectId, Date since, Date until, String memberName) {
        return null;
    }

    @Override
    public List<MergeRequestDto> getMergeRequestsByMemberId(String jwt, int projectId, Date since, Date until, int memberId) {
        return null;
    }
}
