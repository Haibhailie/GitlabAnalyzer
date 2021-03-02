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

    private static MergeRequest mergeRequest;

    private int projectId = 10;
    private String jwt = "";
    private int mergeRequestId = 9;
    private boolean hasConflicts = false;
    private boolean isOpen = true;
    private int userId = 6;
    private String assignedTo = "John";
    private String author = "John";
    private String description = "Random Description";
    private String sourceBranch = "Testing";
    private String targetBranch = "master";
    private int numAdditions = 6;
    private int numDeletions = 12;
    private ArrayList<String> notesName = new ArrayList<>();
    private ArrayList<String> notes = new ArrayList<>();
    private ArrayList<String> committers = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();
    private Date dateSince = new Date();
    private Date dateUntil = new Date();
    private long time = 10000000;

    private boolean isNewFile = true;
    private boolean isDeletedFile = false;
    private boolean isRenamedFile = false;
    private String commitName = "Nerf";
    private String newPath = "root";
    private String oldPath = "";
    private String diff = "";

    @BeforeAll
    public static void setup() {

    }

    @Test
    public void gitlabAPINullTest() {

        when(authenticationService.getGitLabApiFor(jwt)).thenReturn(null);
        gitLabApi = authenticationService.getGitLabApiFor(jwt);

        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
        assertNull(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestId));
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestId));

    }

    public MergeRequest generateTestMergeRequest() {
        MergeRequest tempMergeRequest = new MergeRequest();
        Assignee tempAssignee = new Assignee();
        Author tempAuthor = new Author();

        tempMergeRequest.setIid(mergeRequestId);

        tempMergeRequest.setHasConflicts(hasConflicts);

        if (isOpen)
            tempMergeRequest.setState("opened");
        else
            tempMergeRequest.setState("closed");

        tempAssignee.setName(assignedTo);
        tempAssignee.setId(userId);
        tempMergeRequest.setAssignee(tempAssignee);

        tempAuthor.setName(author);
        tempAuthor.setId(userId);
        tempMergeRequest.setAuthor(tempAuthor);

        tempMergeRequest.setDescription(description);

        tempMergeRequest.setSourceBranch(sourceBranch);

        tempMergeRequest.setTargetBranch(targetBranch);

        tempMergeRequest.setCreatedAt(DateUtils.getDateSinceOrEarliest(time));

        return tempMergeRequest;
    }

}