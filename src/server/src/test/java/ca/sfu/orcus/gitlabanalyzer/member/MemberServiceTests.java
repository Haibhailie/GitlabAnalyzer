package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitService;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestService;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
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
    private static List<Member> members;
    private static CommitStats commitStats;

    private static final String jwt = "";
    private static final int projectId = 5;
    private static final String displayName = "Danny";
    private static final String email = "day@ertyu.com";
    private static final int id = 1;
    private static final String username = "dyu32";
    private static final Date since = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_SINCE));
    private static final Date until = DateUtils.getDateSinceOrEarliest(Long.parseLong(Constants.DEFAULT_UNTIL));
    private static final List<CommitDto> commitDtos = new ArrayList<>();
    private static final List<MergeRequestDto> mergeRequestDtos = new ArrayList<>();

    private static final String authorB = "Ken";
    private static final int userIdB = 7;
    private static final String usernameB = "ken32";
    private static final String authorBEmail = "jjj@verizon.net";

    private static final String assignedToA = "Danny";
    private static final String assignedToB = "Ken";

    private static final String description = "Random Description";
    private static final String sourceBranch = "Testing";
    private static final String targetBranch = "master";

    private static final int mergeRequestIdA = 9;
    private static final int mergeRequestIdB = 8;

    private static final String title = "title";
    private static final String message = "";

    private static final String shaA = "abcd1234";
    private static final String shaB = "efgh5678";

    private static final int count = 10;

    @BeforeAll
    public static void setup() {

    }

    @Test
    public void nullGitLabApiTest() {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);

        assertNull(memberService.getAllMembers(jwt, projectId));
        assertNull(memberService.getAllMembers(gitLabApi, projectId));
        assertNull(memberService.getCommitsByMemberEmail(jwt, projectId, since, until, email));
        assertNull(memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, id));
    }

    @Test
    public void getAllMembersTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(gitLabApi.getProjectApi().getAllMembers(projectId)).thenReturn(members);

        List<MemberDto> memberDtos = memberService.getAllMembers(jwt, projectId);
        List<MemberDto> expectedMemberDtos = new ArrayList<>();

        for (Member m : members) {
            MemberDto presentMember = new MemberDto(m);
            expectedMemberDtos.add(presentMember);
        }

        assertNotNull(memberDtos);
        assertEquals(memberDtos, expectedMemberDtos);
    }

    @Test
    public void getCommitsByMemberEmailTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(commitService.getAllCommits(jwt, projectId, since, until)).thenReturn(commitDtos);

        List<CommitDto> commitsByMemberEmail = memberService.getCommitsByMemberEmail(jwt, projectId, since, until, email);
        List<CommitDto> expectedCommitsByMemberEmail = new ArrayList<>();

        for (CommitDto c : commitsByMemberEmail) {
            if (c.getAuthorEmail().equals(email)) {
                expectedCommitsByMemberEmail.add(c);
            }
        }

        assertNotNull(commitsByMemberEmail);
        assertEquals(commitsByMemberEmail, expectedCommitsByMemberEmail);
    }


    @Test
    public void getMergeRequestsByMemberIDTest() throws GitLabApiException {
        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getProjectApi()).thenReturn(projectApi);
        when(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, since, until, id)).thenReturn(mergeRequestDtos);

        List<MergeRequestDto> mergeRequestByMemberID = memberService.getMergeRequestsByMemberID(jwt, projectId, since, until, id);
        List<MergeRequestDto> expectedMergeRequestByMemberID = mergeRequestDtos;

        assertNotNull(mergeRequestByMemberID);
        assertEquals(mergeRequestByMemberID, expectedMergeRequestByMemberID);
    }


    public static List<Member> getTestMembers() {
        List<Member> tempMembers = new ArrayList<>();
        AbstractUser<Member> memberA = new Member();
        AbstractUser<Member> memberB = new Member();

        memberA.setName(displayName);
        memberA.setEmail(email);
        memberA.setId(id);
        memberA.setUsername(username);

        memberB.setName(authorB);
        memberB.setEmail(authorBEmail);
        memberB.setId(userIdB);
        memberB.setUsername(usernameB);

        tempMembers.add((Member) memberA);
        tempMembers.add((Member) memberB);

        return tempMembers;
    }


    public static List<MergeRequest> getTestMergeRequests() {
        List<MergeRequest> tempMergeRequests = new ArrayList<>();
        MergeRequest tempMergeRequestA = new MergeRequest();
        MergeRequest tempMergeRequestB = new MergeRequest();

        Author tempAuthorA = new Author();
        tempAuthorA.setName(displayName);
        tempAuthorA.setId(id);

        tempMergeRequestA.setAuthor(tempAuthorA);
        tempMergeRequestA.setIid(mergeRequestIdA);

        Assignee tempAssigneeA = new Assignee();
        tempAssigneeA.setName(assignedToB);
        tempAssigneeA.setId(userIdB);
        tempMergeRequestA.setAssignee(tempAssigneeA);

        tempMergeRequestA.setDescription(description);
        tempMergeRequestA.setSourceBranch(sourceBranch);
        tempMergeRequestA.setTargetBranch(targetBranch);


        Author tempAuthorB = new Author();
        tempAuthorB.setName(authorB);
        tempAuthorB.setId(userIdB);
        tempMergeRequestB.setAuthor(tempAuthorB);

        tempMergeRequestB.setIid(mergeRequestIdB);
        tempMergeRequestB.setState("opened");
        Assignee tempAssigneeB = new Assignee();
        tempAssigneeB.setName(assignedToA);
        tempAssigneeB.setId(id);
        tempMergeRequestB.setAssignee(tempAssigneeB);

        tempMergeRequestB.setDescription(description);
        tempMergeRequestB.setSourceBranch(sourceBranch);
        tempMergeRequestB.setTargetBranch(targetBranch);

        tempMergeRequests.add(tempMergeRequestA);
        tempMergeRequests.add(tempMergeRequestB);

        return tempMergeRequests;
    }

    public static List<Commit> gentTestCommits() {


        Commit commitA = new Commit();
        Commit commitB = new Commit();
        List<Commit> tempCommits = new ArrayList<>();

        commitA.setId(String.valueOf(projectId));
        commitA.setTitle(title);
        commitA.setAuthorName(displayName);
        commitA.setAuthorEmail(email);
        commitA.setMessage(message);
        commitA.setId(shaA);
        commitA.setCommittedDate(until);
        commitA.setStats(commitStats);
        commitA.setShortId(shaA);

        commitB.setId(String.valueOf(projectId));
        commitB.setTitle(title);
        commitB.setAuthorName(authorB);
        commitB.setAuthorEmail(authorBEmail);
        commitB.setMessage(message);
        commitB.setId(shaB);
        commitB.setCommittedDate(until);
        commitB.setStats(commitStats);
        commitB.setShortId(shaB);

        tempCommits.add(commitA);
        tempCommits.add(commitB);
        return tempCommits;
    }

    public static CommitStats getTestCommitStats() {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(count);
        commitStats.setDeletions(count);
        commitStats.setTotal(count*2);

        return commitStats;
    }





















}