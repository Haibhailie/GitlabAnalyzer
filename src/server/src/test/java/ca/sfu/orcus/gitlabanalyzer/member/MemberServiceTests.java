package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
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
public class MemberServiceTests {
    @Mock private MemberRepository memberRepository;
    @Mock private AuthenticationService authenticationService;
    @Mock private MergeRequestService mergeRequestService;
    @Mock private CommitService commitService;
    @Mock private GitLabApi gitLabApi;
    @Mock private ProjectApi projectApi;
    @Mock private RepositoryApi repositoryApi;

    // Class to be tested
    @InjectMocks
    private MemberService memberService;

    // Test objects
    private static Member member;

    private static final String jwt = "";
    private static final int projectId = 5;
    private static final String displayName = "Danny";
    private static final String email = "day@ertyu.com";
    private static final int id = 1;
    private static final String username = "dyu32";
    private static final String role = "Maintainer";
    private static final Date since = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_SINCE));
    private static final Date until = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_UNTIL));
    private static Project project;
    private static final List<MemberDto> memberList = new ArrayList<>();

    @BeforeAll
    public static void setup() {

    }

    @Test
    public void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        assertNull(memberService.getAllMembers(jwt, projectId));
        assertNull(memberService.getCommitsByMemberEmail(jwt, projectId, since, until, email));
        assertNull(memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, id));
    }

    @Test
    public void getAllMembersTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(projectApi.getProject(projectId)).thenReturn(project);

        List<MemberDto> memberDtos = memberService.getAllMembers(jwt, projectId);
        List<MemberDto> expectedMemberDtos = new ArrayList<>();

        assertNotNull(memberDtos);
        assertEquals(memberDtos, expectedMemberDtos);
    }

    //TODO: get a single member?















}