package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectTests {
    // Class to be tested
    private ProjectService projectService;

    // Dependencies (will be mocked)
    private ProjectRepository projectRepository;
    private AuthenticationService authenticationService;
    private MemberService memberService;
    private GitLabApi gitLabApi;
    private ProjectApi projectApi;
    private RepositoryApi repositoryApi;

    // Test objects, maybe move to separate classes
    private static Project project;
    private static ProjectStatistics projectStatistics;

    private static final String jwt = "";
    private static final int projectId = 5;
    private static final String projectName = "ProjectName";
    private static final long count = 10;
    private static final Date projectCreatedAt = new Date();
    private static final List<MemberDto> memberDtos = new ArrayList<>();
    private static final List<Branch> branches = new ArrayList<>();

    @Before
    public void setup() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        authenticationService = Mockito.mock(AuthenticationService.class);
        memberService = Mockito.mock(MemberService.class);
        gitLabApi = Mockito.mock(GitLabApi.class);
        projectApi = Mockito.mock(ProjectApi.class);
        repositoryApi = Mockito.mock(RepositoryApi.class);

        projectStatistics = getTestProjectStatistics();
        project = getTestProject();

        projectService = new ProjectService(projectRepository, authenticationService, memberService);
    }

    @Test
    public void nullGitLabApiTest() {
        Mockito.when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        Assertions.assertNull(projectService.getProject(jwt, projectId));
        Assertions.assertNull(projectService.getAllProjects(jwt));
    }

    @Test
    public void getSingleProjectTest() throws GitLabApiException {
        Mockito.when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        Mockito.when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        Mockito.when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        Mockito.when(projectApi.getProject(projectId, true)).thenReturn(project);
        Mockito.when(repositoryApi.getBranches(projectId)).thenReturn(branches);
        Mockito.when(memberService.getAllMembers(gitLabApi, projectId)).thenReturn(memberDtos);

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, projectId);
        ProjectExtendedDto expectedProjectExtendedDto = new ProjectExtendedDto(project, memberDtos, branches.size());

        Assertions.assertNotNull(projectExtendedDto);
        Assertions.assertEquals(projectExtendedDto, expectedProjectExtendedDto);
    }

    // Can/should be move to another class
    public Project getTestProject() {
        Project project = new Project();

        project.setId(projectId);
        project.setName(projectName);
        project.setStatistics(projectStatistics);
        project.setCreatedAt(projectCreatedAt);

        return project;
    }

    // Can/should be move to another class
    public ProjectStatistics getTestProjectStatistics() {
        ProjectStatistics projectStatistics = new ProjectStatistics();

        projectStatistics.setCommitCount(count);
        projectStatistics.setStorageSize(count);
        projectStatistics.setRepositorySize(count);
        projectStatistics.setWikiSize(count);
        projectStatistics.setLfsObjectsSize(count);
        projectStatistics.setJobArtifactsSize(count);

        return projectStatistics;
    }
}
