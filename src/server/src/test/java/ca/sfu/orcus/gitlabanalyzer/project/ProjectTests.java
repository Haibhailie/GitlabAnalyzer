package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.mocks.models.ProjectMock;
import ca.sfu.orcus.gitlabanalyzer.mocks.models.ProjectStatisticsMock;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectStatistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectTests {
    @Mock private ProjectRepository projectRepository;
    @Mock private AuthenticationService authenticationService;
    @Mock private MemberService memberService;

    @InjectMocks
    private ProjectService projectService;

    private static GitLabApi gitLabApi;

    // TODO: Optionally get them from model package later (?)
    private static final String jwt = "";
    private static final List<MemberDto> memberDtos = new ArrayList<>();
    private static final List<Branch> branches = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
    }

    @Test
    public void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(anyString())).thenReturn(null);

        assertNull(projectService.getProject(jwt, ProjectMock.defaultId));
        assertNull(projectService.getAllProjects(jwt));
    }

    @Test
    public void getSingleProjectTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        ProjectStatistics projectStatistics = ProjectStatisticsMock.createProjectStatistics();
        Project project = ProjectMock.createProject(projectStatistics);
        when(gitLabApi.getProjectApi().getProject(ProjectMock.defaultId, true)).thenReturn(project);
        when(gitLabApi.getRepositoryApi().getBranches(ProjectMock.defaultId)).thenReturn(branches);
        when(memberService.getAllMembers(gitLabApi, ProjectMock.defaultId)).thenReturn(memberDtos);

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, ProjectMock.defaultId);
        ProjectExtendedDto expectedProjectExtendedDto = new ProjectExtendedDto(project, memberDtos, branches.size());

        // Check
        assertEquals(projectExtendedDto, expectedProjectExtendedDto);
    }

    @Test
    public void getAllProjectsTest() {
    }
}
