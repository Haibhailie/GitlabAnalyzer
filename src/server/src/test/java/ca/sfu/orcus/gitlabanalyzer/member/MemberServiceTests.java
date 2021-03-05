package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.Constants;
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

    private static final String defaultBranch = "master";
    private static Project project;

    @BeforeAll
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
        project = new Project();
        project.setDefaultBranch(defaultBranch);

    }

    @Test
    public void nullGitLabApiTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);

        assertNull(memberService.getAllMembers(jwt, projectId));
        assertNull(memberService.getCommitsByMemberEmail(jwt, projectId, since, until, MemberMock.defaultEmail));
        assertNull(memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, MemberMock.defaultId));
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
    public void getMergeRequestsByMemberIDTest() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<MergeRequest> mergeRequests = MergeRequestMock.generateTestMergeRequestList();
        List<MergeRequestDto> mergeRequestDtos = new ArrayList<>();
        for (MergeRequest mr : mergeRequests) {
            mergeRequestDtos.add(new MergeRequestDto(gitLabApi, projectId, mr));
        }

        int id = MemberMock.defaultId;
        when(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, since, until, id)).thenReturn(mergeRequestDtos);

        List<MergeRequestDto> mergeRequestByMemberID = memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, id);
        List<MergeRequestDto> expectedMergeRequestByMemberID = mergeRequestDtos;

        assertNotNull(mergeRequestByMemberID);
        assertEquals(mergeRequestByMemberID, expectedMergeRequestByMemberID);
    }

    @Test
    public void getCommitsByMemberEmailTest() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<Commit> commits = CommitMock.createTestCommitList();
        List<CommitDto> commitDtos = new ArrayList<>();
        for (Commit c : commits) {
            commitDtos.add(new CommitDto(gitLabApi, projectId, c));
        }

        String email = MemberMock.defaultEmail;
        when(commitService.getAllCommitDtos(gitLabApi, projectId, since, until,email)).thenReturn(commitDtos);

        List<CommitDto> commitsByMemberEmail = memberService.getCommitsByMemberEmail(jwt, projectId, since, until, email);
        List<CommitDto> expectedCommitsByMemberEmail = commitDtos;

        assertNotNull(commitsByMemberEmail);
        assertEquals(commitsByMemberEmail, expectedCommitsByMemberEmail);
    }



















}