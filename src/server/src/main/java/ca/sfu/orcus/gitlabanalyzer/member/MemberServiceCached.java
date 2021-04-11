package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceCached {
    GitLabApiWrapper gitLabApiWrapper;
    ProjectRepository projectRepo;
    MemberRepository memberRepo;

    @Autowired
    MemberServiceCached(GitLabApiWrapper gitLabApiWrapper, MemberRepository memberRepo, ProjectRepository projectRepo) {
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.projectRepo = projectRepo;
        this.memberRepo = memberRepo;
    }

    public List<MemberDtoDb> getAllMembers(String jwt, int projectId) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        if (projectUrl.isEmpty()) {
            return null;
        }
        List<String> memberDocumentIds = projectRepo.getMemberDocIds(projectId, projectUrl.get());
        return memberRepo.getMembers(memberDocumentIds);
    }
}
