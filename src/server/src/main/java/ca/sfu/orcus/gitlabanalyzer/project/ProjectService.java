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
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public ProjectService(ProjectRepository projectRepo,
                          GitLabApiWrapper gitLabApiWrapper) {
        this.projectRepo = projectRepo;
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
                ProjectDtoDb projectDto = createProjectDto(gitLabApi, p);
                projectDtos.add(projectDto);
            }
            return projectDtos;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private ProjectDtoDb createProjectDto(GitLabApi gitLabApi, Project project) throws GitLabApiException {
        String memberRole = getAuthenticatedMembersRoleInProject(gitLabApi, project.getId());
        long lastAnalysisTime = projectRepo.getLastAnalysisTimeForProject(project.getId(),
                VariableDecoderUtil.decode("GITLAB_URL"));
        return new ProjectDtoDb(project, memberRole, lastAnalysisTime, new ArrayList<>());
    }

    private String getAuthenticatedMembersRoleInProject(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        int currentUserId = gitLabApi.getUserApi().getCurrentUser().getId();
        Member currentMember = gitLabApi.getProjectApi().getMember(projectId, currentUserId);
        int currentAccessLevel = currentMember.getAccessLevel().value;
        return MemberUtils.getMemberRoleFromAccessLevel(currentAccessLevel);
    }

    public Optional<ProjectDtoDb> getProject(String jwt, int projectId) {
        Optional<String> projectUrl = gitLabApiWrapper.getProjectUrl(jwt, projectId);
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (projectUrl.isEmpty() || gitLabApi == null) {
            return Optional.empty();
        }

        return getProject(gitLabApi, projectId, projectUrl.get());
    }

    private Optional<ProjectDtoDb> getProject(GitLabApi gitLabApi, int projectId, String projectUrl) {
        try {
            Project project = gitLabApi.getProjectApi().getProject(projectId);
            if (projectRepo.projectIsAlreadyCached(projectUrl)) {
                return getProjectFromRepo(gitLabApi, project);
            } else {
                return Optional.of(createProjectDto(gitLabApi, project));
            }
        } catch (GitLabApiException e) {
            return Optional.empty();
        }
    }

    private Optional<ProjectDtoDb> getProjectFromRepo(GitLabApi gitLabApi, Project project) throws GitLabApiException {
        Optional<ProjectDtoDb> projectOptional = projectRepo.getProject(project.getWebUrl());
        if (projectOptional.isPresent()) {
            ProjectDtoDb projectDto = projectOptional.get();
            projectDto.setRole(getAuthenticatedMembersRoleInProject(gitLabApi, project.getId()));
            projectDto.setLastActivityTime(project.getLastActivityAt().getTime());
            return Optional.of(projectDto);
        } else {
            return projectOptional;
        }

    }
}
