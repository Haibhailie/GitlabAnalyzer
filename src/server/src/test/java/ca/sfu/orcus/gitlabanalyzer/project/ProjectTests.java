package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
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
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectTests {
    @Mock private ProjectRepository projectRepository;
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private MemberService memberService;

    @InjectMocks
    private ProjectService projectService;

    private GitLabApi gitLabApi;
    private static final String jwt = "jwt";

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
    }

    @Test
    public void getProjectWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(projectService.getProject(jwt, ProjectMock.defaultId));
    }

    @Test
    public void getAllProjectsWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(projectService.getAllProjects(jwt));
    }

    @Test
    public void getSingleProject() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        int projectId = ProjectMock.defaultId;
        addSingleProject(projectId);

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, projectId);
        ProjectExtendedDto expectedProjectExtendedDto = getExpectedProjectExtendedDto(projectId);

        assertEquals(projectExtendedDto, expectedProjectExtendedDto);
    }

    @Test
    public void getSingleProjectIfGitLabApiThrows() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getProject(ProjectMock.defaultId, true))
                .thenThrow(new GitLabApiException("GitLabApi threw up"));

        ProjectExtendedDto projectExtendedDto = projectService.getProject(jwt, ProjectMock.defaultId);

        assertNull(projectExtendedDto);
    }

    @Test
    public void getAllProjects() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        addManyProjects();

        List<ProjectDto> projectDtos = projectService.getAllProjects(jwt);
        List<ProjectDto> expectedProjectDtos = getExpectedProjectDtos();

        assertEquals(projectDtos, expectedProjectDtos);
    }

    @Test
    public void getAllProjectsIfGitLabApiThrows() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getMemberProjects())
                .thenThrow(new GitLabApiException("GitLabApi threw up"));

        List<ProjectDto> projectDtos = projectService.getAllProjects(jwt);

        assertNull(projectDtos);
    }

    // Helper functions

    private void addSingleProject(int projectId) throws GitLabApiException {
        Project project = ProjectMock.createProject();
        when(gitLabApi.getProjectApi().getProject(projectId, true)).thenReturn(project);

        initializeProjectMembers(projectId);
        initializeProjectBranches(projectId);
    }

    private void initializeProjectMembers(int projectId) {
        List<MemberDto> memberDtos = new ArrayList<>();
        when(memberService.getAllMembers(gitLabApi, projectId)).thenReturn(memberDtos);
    }

    private void initializeProjectBranches(int projectId) throws GitLabApiException {
        List<Branch> branches = new ArrayList<>();
        when(gitLabApi.getRepositoryApi().getBranches(projectId)).thenReturn(branches);
    }

    private ProjectExtendedDto getExpectedProjectExtendedDto(int projectId) throws GitLabApiException {
        Project project = gitLabApi.getProjectApi().getProject(projectId, true);
        List<MemberDto> memberDtos = memberService.getAllMembers(gitLabApi, projectId);
        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(projectId);

        return new ProjectExtendedDto(project, memberDtos, branches.size());
    }

    private void addManyProjects() throws GitLabApiException {
        User user = UserMock.createUser();
        when(gitLabApi.getUserApi().getCurrentUser()).thenReturn(user);

        List<Project> manyProjects = new ArrayList<>();
        int userId = user.getId();

        for (int i = 0; i < 10; i++) {
            Project project = ProjectMock.createProject();
            manyProjects.add(project);
            addUserToProject(userId, project.getId());
        }

        when(gitLabApi.getProjectApi().getMemberProjects()).thenReturn(manyProjects);
    }

    private void addUserToProject(int userId, int projectId) throws GitLabApiException {
        Member member = MemberMock.createMember(MemberMock.defaultDisplayName, MemberMock.defaultEmail, userId, MemberMock.defaultUserName, MemberMock.defaultAccessLevel);
        when(gitLabApi.getProjectApi().getMember(projectId, userId)).thenReturn(member);
    }

    private List<ProjectDto> getExpectedProjectDtos() throws GitLabApiException {
        List<ProjectDto> expectedProjectDtos = new ArrayList<>();
        String defaultRole = MemberUtils.getMemberRoleFromAccessLevel(MemberMock.defaultAccessLevel.value);

        List<Project> projects = gitLabApi.getProjectApi().getMemberProjects();
        for (Project p : projects) {
            expectedProjectDtos.add(new ProjectDto(p, defaultRole));
        }

        return expectedProjectDtos;
    }
}
