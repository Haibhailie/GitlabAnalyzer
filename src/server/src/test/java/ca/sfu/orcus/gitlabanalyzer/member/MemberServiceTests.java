package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.*;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private MergeRequestService mergeRequestService;
    @Mock private CommitService commitService;
    @Mock private ProjectApi projectApi;

    // Class to be tested
    @InjectMocks
    private MemberService memberService;

    private GitLabApi gitLabApi;
    private static final String jwt = UUID.randomUUID().toString();

    // Test objects
    private static final int projectId = ProjectMock.defaultId;
    private static final Date since = CommitMock.defaultDate;
    private static final Date until = CommitMock.defaultDate;

    private static final ProjectStatistics projectStatistics = ProjectStatisticsMock.createProjectStatistics();
    private static Project project;

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
        project = ProjectMock.createProject(projectStatistics);

    }

    // Testing the null checks
    @Test
    public void getAllMembersWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(memberService.getAllMembers(jwt, projectId));
    }

    @Test
    public void getMergeRequestsByMemberIDWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, MemberMock.defaultId));
    }

    @Test
    public void getCommitsByMemberEmailWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(memberService.getCommitsByMemberEmail(jwt, projectId, since, until, MemberMock.defaultEmail));
    }

    @Test
    public void getAllMembersTest() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);

        List<Member> memberList = MemberMock.createTestMemberList();

        when(projectApi.getAllMembers(projectId)).thenReturn(memberList);

        List<MemberDto> memberDtos = memberService.getAllMembers(jwt, projectId);

        List<MemberDto> expectedMemberDtos = new ArrayList<>();
        for (Member m : memberList) {
            expectedMemberDtos.add(new MemberDto(m));
        }

        assertNotNull(memberDtos);
        assertEquals(memberDtos, expectedMemberDtos);
    }

    @Test
    public void getMergeRequestsByMemberIDTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<MergeRequest> mergeRequests = MergeRequestMock.generateTestMergeRequestList();
        List<MergeRequestDto> mergeRequestDtos = new ArrayList<>();

        int id = MemberMock.defaultId;
        when(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, since, until, id)).thenReturn(mergeRequestDtos);

        List<MergeRequestDto> mergeRequestByMemberID = memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, id);
        List<MergeRequestDto> expectedMergeRequestByMemberID = mergeRequestDtos;

        assertNotNull(mergeRequestByMemberID);
        assertEquals(mergeRequestByMemberID, expectedMergeRequestByMemberID);
    }

    @Test
    public void getCommitsByMemberEmailTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<Commit> commitList = CommitMock.createTestCommitList();
        CommitStats commitStats = CommitStatsMock.createCommitStats();
        Commit commit = CommitMock.createCommit(commitStats);
        List<CommitDto> commitDtos = new ArrayList<>();

        String email = MemberMock.defaultEmail;
        when(commitService.getAllCommitDtos(gitLabApi, projectId, since, until, email)).thenReturn(commitDtos);

        List<CommitDto> commitsByMemberEmail = memberService.getCommitsByMemberEmail(jwt, projectId, since, until, email);
        List<CommitDto> expectedCommitsByMemberEmail = commitDtos;

        assertNotNull(commitsByMemberEmail);
        assertEquals(commitsByMemberEmail, expectedCommitsByMemberEmail);
    }

}