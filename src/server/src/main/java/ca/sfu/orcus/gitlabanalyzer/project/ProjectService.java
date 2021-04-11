package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
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

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          GitLabApiWrapper gitLabApiWrapper) {
        this.projectRepository = projectRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<ProjectDtoDb> getAllProjects(String jwt) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getAllProjects(gitLabApi);
        } else {
            return null;
        }
    }

    private ArrayList<ProjectDtoDb> getAllProjects(GitLabApi gitLabApi) {
        try {
            ArrayList<ProjectDtoDb> projectDtos = new ArrayList<>();
            List<Project> projects = gitLabApi.getProjectApi().getMemberProjects();
            for (Project p : projects) {
                ProjectDtoDb projectDto = createProjectSkeletonDto(gitLabApi, p);
                projectDtos.add(projectDto);
                projectRepository.cacheProjectSkeleton(projectDto);
            }
            return projectDtos;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private ProjectDtoDb createProjectSkeletonDto(GitLabApi gitLabApi, Project project) throws GitLabApiException {
        String memberRole = getAuthenticatedMembersRoleInProject(gitLabApi, project.getId());
        long lastAnalysisTime = projectRepository.getLastAnalysisTimeForProject(project.getId(),
                VariableDecoderUtil.decode("GITLAB_URL"));
        return new ProjectDtoDb(project, memberRole, lastAnalysisTime, new ArrayList<>());
    }

    private String getAuthenticatedMembersRoleInProject(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        int currentUserId = gitLabApi.getUserApi().getCurrentUser().getId();
        Member currentMember = gitLabApi.getProjectApi().getMember(projectId, currentUserId);
        int currentAccessLevel = currentMember.getAccessLevel().value;
        return MemberUtils.getMemberRoleFromAccessLevel(currentAccessLevel);
    }
}
