package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberServiceDirect;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectMock;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
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
public class ProjectServiceTests {
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private MemberServiceDirect memberService;

    @InjectMocks
    private ProjectService projectService;

    private GitLabApi gitLabApi;
    private static final String jwt = "jwt";

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
    }

    @Test
    public void getAllProjectsWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(projectService.getAllProjects(jwt));
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
        when(memberService.getAllMembers(jwt, projectId)).thenReturn(memberDtos);
    }

    private void initializeProjectBranches(int projectId) throws GitLabApiException {
        List<Branch> branches = new ArrayList<>();
        when(gitLabApi.getRepositoryApi().getBranches(projectId)).thenReturn(branches);
    }

    private ProjectExtendedDto getExpectedProjectExtendedDto(int projectId) throws GitLabApiException {
        Project project = gitLabApi.getProjectApi().getProject(projectId, true);
        List<MemberDto> memberDtos = memberService.getAllMembers(jwt, projectId);
        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(projectId);

        return new ProjectExtendedDto(project, memberDtos, branches.size());
    }
}
