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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectTests {
    @Mock private ProjectRepository projectRepository;
    @Mock private AuthenticationService authenticationService;
    @Mock private MemberService memberService;
    @Mock private GitLabApi gitLabApi;
    @Mock private ProjectApi projectApi;
    @Mock private RepositoryApi repositoryApi;

    // Class to be tested
    @InjectMocks
    private ProjectService projectService;

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

    @BeforeAll
    public static void setup() {
        projectStatistics = getTestProjectStatistics();
        project = getTestProject();
    }

    @Test
    public void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        assertNull(projectService.getProject(jwt, projectId));
        assertNull(projectService.getAllProjects(jwt));
    }

    @Test
    public void getSingleProjectTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        when(projectApi.getProject(projectId, true)).thenReturn(project);
        when(repositoryApi.getBranches(projectId)).thenReturn(branches);
        when(memberService.getAllMembers(gitLabApi, projectId)).thenReturn(memberDtos);

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, projectId);
        ProjectExtendedDto expectedProjectExtendedDto = new ProjectExtendedDto(project, memberDtos, branches.size());

        assertNotNull(projectExtendedDto);
        assertEquals(projectExtendedDto, expectedProjectExtendedDto);
    }

    // Can/should be move to another class
    public static Project getTestProject() {
        Project project = new Project();

        project.setId(projectId);
        project.setName(projectName);
        project.setStatistics(projectStatistics);
        project.setCreatedAt(projectCreatedAt);

        return project;
    }

    // Can/should be move to another class
    public static ProjectStatistics getTestProjectStatistics() {
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
