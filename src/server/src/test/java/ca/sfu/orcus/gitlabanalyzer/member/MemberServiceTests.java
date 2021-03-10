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

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private MergeRequestService mergeRequestService;
    @Mock private CommitService commitService;

    // Class to be tested
    @InjectMocks
    private MemberService memberService;

    private GitLabApi gitLabApi;
    private final String jwt = "jwt";

    // Test objects
    private static final int projectId = ProjectMock.defaultId;
    private static final Date since = CommitMock.defaultDate;
    private static final Date until = CommitMock.defaultDate;

    @BeforeEach
    public void setup() {
        gitLabApi = GitLabApiMock.getGitLabApiMock();
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
        assertNull(memberService.getMergeRequestsByMemberId(jwt, projectId, since, until, MemberMock.defaultId));
    }

    @Test
    public void getCommitsByMemberEmailWithNullGitLabApi() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        assertNull(memberService.getCommitsByMemberName(jwt, projectId, since, until, MemberMock.defaultEmail));
    }

    // Testing the MemberService methods
    @Test
    public void getAllMembersTest() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<Member> memberList = MemberMock.createTestMemberList();

        when(gitLabApi.getProjectApi().getAllMembers(projectId)).thenReturn(memberList);

        List<MemberDto> memberDtos = memberService.getAllMembers(jwt, projectId);

        List<MemberDto> expectedMemberDtos = new ArrayList<>();
        for (Member m : memberList) {
            expectedMemberDtos.add(new MemberDto(m));
        }

        assertEquals(memberDtos, expectedMemberDtos);
    }

    @Test
    public void getCommitsByMemberEmailTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<CommitDto> commitDtos = new ArrayList<>();

        String email = MemberMock.defaultEmail;
        when(commitService.returnAllCommits(gitLabApi, projectId, since, until, email)).thenReturn(commitDtos);

        List<CommitDto> commitsByMemberEmail = memberService.getCommitsByMemberName(jwt, projectId, since, until, email);

        assertEquals(commitsByMemberEmail, commitDtos);
    }

    @Test
    public void getMergeRequestsByMemberIdTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);

        List<MergeRequestDto> mergeRequestDtos = new ArrayList<>();

        int memberId = MemberMock.defaultId;
        when(mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, since, until, memberId)).thenReturn(mergeRequestDtos);

        List<MergeRequestDto> mergeRequestByMemberId = memberService.getMergeRequestsByMemberId(jwt, projectId, since, until, memberId);

        assertEquals(mergeRequestByMemberId, mergeRequestDtos);
    }

    @Test
    public void getAllMembersException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getAllMembers(projectId)).thenThrow(GitLabApiException.class);
        assertNull(memberService.getAllMembers(jwt, projectId));
    }

}