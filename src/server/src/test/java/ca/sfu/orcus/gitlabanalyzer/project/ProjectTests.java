package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.MemberMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectStatisticsMock;
import ca.sfu.orcus.gitlabanalyzer.models.UserMock;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectTests {
    @Mock private ProjectRepository projectRepository;
    @Mock private AuthenticationService authService;
    @Mock private MemberService memberService;

    @InjectMocks
    private ProjectService projectService;

    private GitLabApi gitLabApi;
    private static final String jwt = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
    }

    @Test
    public void getProjectWithNullGitLabApi() {
        when(authService.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(projectService.getProject(jwt, ProjectMock.defaultId));
    }

    @Test
    public void getAllProjectsWithNullGitLabApi() {
        when(authService.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(projectService.getAllProjects(jwt));
    }

    @Test
    public void getSingleProject() throws GitLabApiException {
        when(authService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        ProjectStatistics projectStatistics = ProjectStatisticsMock.createProjectStatistics();
        Project project = ProjectMock.createProject(projectStatistics);
        when(gitLabApi.getProjectApi().getProject(ProjectMock.defaultId, true)).thenReturn(project);

        List<MemberDto> memberDtos = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();
        when(gitLabApi.getRepositoryApi().getBranches(ProjectMock.defaultId)).thenReturn(branches);
        when(memberService.getAllMembers(gitLabApi, ProjectMock.defaultId)).thenReturn(memberDtos);

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, ProjectMock.defaultId);
        ProjectExtendedDto expectedProjectExtendedDto = new ProjectExtendedDto(project, memberDtos, branches.size());

        assertEquals(projectExtendedDto, expectedProjectExtendedDto);
    }

    @Test
    public void getSingleProjectIfGitLabApiThrows() throws GitLabApiException {
        when(authService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getProject(ProjectMock.defaultId, true))
                .thenThrow(new GitLabApiException("GitLabApi threw up"));

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, ProjectMock.defaultId);

        assertNull(projectExtendedDto);
    }

    @Test
    public void getAllProjects() throws GitLabApiException {
        when(authService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        User user = UserMock.createUser();
        when(gitLabApi.getUserApi().getCurrentUser()).thenReturn(user);

        List<ProjectDto> expectedProjectDtos = new ArrayList<>();
        List<Project> memberProjects = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ProjectStatistics projectStatistics = ProjectStatisticsMock.createProjectStatistics();
            Project project = ProjectMock.createProject(projectStatistics);
            memberProjects.add(project);

            Member member = MemberMock.createMember(user.getId(), MemberMock.defaultAccessLevel);
            when(gitLabApi.getProjectApi().getMember(project.getId(), user.getId())).thenReturn(member);

            String role = MemberUtils.getMemberRoleFromAccessLevel(MemberMock.defaultAccessLevel.value);
            expectedProjectDtos.add(new ProjectDto(project, role));
        }

        when(gitLabApi.getProjectApi().getMemberProjects()).thenReturn(memberProjects);

        List<ProjectDto> projectDtos = projectService.getAllProjects(jwt);

        assertEquals(projectDtos, expectedProjectDtos);
    }

    @Test
    public void getAllProjectsIfGitLabApiThrows() throws GitLabApiException {
        when(authService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getMemberProjects())
                .thenThrow(new GitLabApiException("GitLabApi threw up"));

        List<ProjectDto> projectDtos = projectService.getAllProjects(jwt);

        assertNull(projectDtos);
    }
}
