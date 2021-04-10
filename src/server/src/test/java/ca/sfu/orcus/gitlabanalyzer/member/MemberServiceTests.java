package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import ca.sfu.orcus.gitlabanalyzer.models.ProjectMock;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTests {
    @Mock private GitLabApiWrapper gitLabApiWrapper;
    @Mock private MergeRequestService mergeRequestService;

    // Class to be tested
    @InjectMocks
    private MemberServiceDirect memberService;

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
    public void getAllMembersException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi().getAllMembers(projectId)).thenThrow(GitLabApiException.class);
        assertNull(memberService.getAllMembers(jwt, projectId));
    }

}