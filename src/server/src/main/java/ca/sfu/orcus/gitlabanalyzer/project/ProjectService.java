package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private final MemberService memberService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, GitLabApiWrapper gitLabApiWrapper, MemberService memberService) {
        this.projectRepository = projectRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.memberService = memberService;
    }

    public List<ProjectDto> getAllProjects(String jwt) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getAllProjects(gitLabApi);
        } else {
            return null;
        }
    }

    private ArrayList<ProjectDto> getAllProjects(GitLabApi gitLabApi) {
        try {
            ArrayList<ProjectDto> projectDtos = new ArrayList<>();
            List<Project> projects = gitLabApi.getProjectApi().getMemberProjects();
            for (Project p : projects) {
                ProjectDto projectDto = getProjectDto(gitLabApi, p);
                projectDtos.add(projectDto);
                projectRepository.cacheProjectSkeleton(projectDto, p.getVisibility());
            }
            return projectDtos;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private ProjectDto getProjectDto(GitLabApi gitLabApi, Project project) throws GitLabApiException {
        String memberRole = getAuthenticatedMembersRoleInProject(gitLabApi, project.getId());
        long lastAnalysisTime = projectRepository.getLastAnalysisTimeForProject(project.getId(),
                VariableDecoderUtil.decode("GITLAB_URL"));
        return new ProjectDto(project, memberRole, lastAnalysisTime);
    }

    public ProjectExtendedDto getProject(String jwt, int projectId) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getProject(gitLabApi, projectId);
        } else {
            return null;
        }
    }

    private ProjectExtendedDto getProject(GitLabApi gitLabApi, int projectId) {
        try {
            Project project = gitLabApi.getProjectApi().getProject(projectId, true);
            long numBranches = gitLabApi.getRepositoryApi().getBranches(projectId).size();
            List<MemberDto> memberDtos = memberService.getAllMembers(gitLabApi, projectId);
            return new ProjectExtendedDto(project, memberDtos, numBranches);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private String getAuthenticatedMembersRoleInProject(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        int currentUserId = gitLabApi.getUserApi().getCurrentUser().getId();
        Member currentMember = gitLabApi.getProjectApi().getMember(projectId, currentUserId);
        int currentAccessLevel = currentMember.getAccessLevel().value;
        return MemberUtils.getMemberRoleFromAccessLevel(currentAccessLevel);
    }
}
