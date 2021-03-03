package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MergeRequestServiceTest {

    @InjectMocks
    private MergeRequestService mergeRequestService;

    @Mock
    private MergeRequestRepository mergeRequestRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private GitLabApi gitLabApi;
    @Mock
    private MergeRequestApi mergeRequestApi;
    @Mock
    private CommitsApi commitsApi;
    @Mock
    private Diff diffApi;
    @Mock
    private NotesApi notesApi;

    private static List<MergeRequest> mergeRequests;

    private static int projectId = 10;
    private static String jwt = "";
    private static int mergeRequestId = 9;
    private static boolean hasConflicts = false;
    private static boolean isOpen = true;
    private static int userId = 6;
    private static int userIdB = 7;
    private static String assignedTo = "John";
    private static String author = "John";
    private static String description = "Random Description";
    private static String sourceBranch = "Testing";
    private static String targetBranch = "master";
    private static int numAdditions = 6;
    private static int numDeletions = 12;
    private static ArrayList<String> notesName = new ArrayList<>();
    private static ArrayList<String> notes = new ArrayList<>();
    private static ArrayList<String> committers = new ArrayList<>();
    private static List<Participant> participants = new ArrayList<>();
    private static Date dateSince = new Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000);
    private static Date dateNow = new Date();
    private static Date dateUntil = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000);
    private static long time = 10000000;

    private static boolean isNewFile = true;
    private static boolean isDeletedFile = false;
    private static boolean isRenamedFile = false;
    private static String commitName = "Nerf";
    private static String newPath = "root";
    private static String oldPath = "";
    private static String diff = "";

    @BeforeAll
    public static void setup() {
        mergeRequests = generateTestMergeRequestList();
    }

    @Test
    public void gitlabAPIPrimaryNullTest() {

        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);
        gitLabApi = authenticationService.getGitLabApiFor(jwt);

        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
        assertNull(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestId));
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestId));
    }

    @Test
    public void getAllMergeRequestWithoutMemberID() throws GitLabApiException {

        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);

        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);

        List<MergeRequestDto> expectedMergeRequestDtoList = new ArrayList<>();
        for (MergeRequest m : mergeRequests)
            expectedMergeRequestDtoList.add(new MergeRequestDto(gitLabApi, projectId, m));

        assertNotNull(mergeRequestDtoList);
        assertEquals(expectedMergeRequestDtoList.size(), mergeRequestDtoList.size());
        assertEquals(mergeRequestDtoList, expectedMergeRequestDtoList);
    }


    public static List<MergeRequest> generateTestMergeRequestList() {
        MergeRequest tempMergeRequest = new MergeRequest();

        Author tempAuthor = new Author();
        tempAuthor.setName(author);
        tempAuthor.setId(userId);
        tempMergeRequest.setAuthor(tempAuthor);

        tempMergeRequest.setIid(mergeRequestId);
        tempMergeRequest.setHasConflicts(hasConflicts);
        tempMergeRequest.setState("opened");
        Assignee tempAssignee = new Assignee();
        tempAssignee.setName(assignedTo);
        tempAssignee.setId(userId);
        tempMergeRequest.setAssignee(tempAssignee);

        tempMergeRequest.setDescription(description);
        tempMergeRequest.setSourceBranch(sourceBranch);
        tempMergeRequest.setTargetBranch(targetBranch);
        tempMergeRequest.setCreatedAt(dateNow);
        tempMergeRequest.setHasConflicts(false);
        tempMergeRequest.setMergedAt(dateNow);

        List<MergeRequest> tempMergeRequestList = new ArrayList<>();
        tempMergeRequestList.add(tempMergeRequest);

        tempAuthor.setId(userIdB);
        tempMergeRequest.setAuthor(tempAuthor);
        tempMergeRequestList.add(tempMergeRequest);

        return tempMergeRequestList;
    }

}