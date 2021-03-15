package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.CommitMock;
import ca.sfu.orcus.gitlabanalyzer.models.DiffMock;
import ca.sfu.orcus.gitlabanalyzer.models.MergeRequestMock;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.gitlab4j.api.models.MergeRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MergeRequestServiceTest extends MergeRequestMock {

    @InjectMocks
    private MergeRequestService mergeRequestService;

    @Mock
    private GitLabApiWrapper gitLabApiWrapper;

    @Mock
    private MergeRequest mergeRequest;

    private GitLabApi gitLabApi = GitLabApiMock.getGitLabApiMock();
    private final MergeRequestApi mergeRequestApi = gitLabApi.getMergeRequestApi();
    private final CommitsApi commitsApi = gitLabApi.getCommitsApi();
    private final NotesApi notesApi = gitLabApi.getNotesApi();

    static List<MergeRequest> mergeRequests;
    static List<Commit> commits;
    static List<Diff> diffs;

    public void initialMergeRequestTestSetup() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
    }

    public void initialNullCheckSetup() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
    }

    @BeforeAll
    public static void setup() {
        mergeRequests = MergeRequestMock.createTestMergeRequestList();
        commitStats = CommitMock.createCommitStats();
        commits = MergeRequestMock.createMergeRequestTestCommitList();
        diffs = DiffMock.createTestDiffList();
    }

    @Test
    public void gitlabApiGetAllMergeRequestsNullTest() {
        initialNullCheckSetup();
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
    }

    @Test
    public void gitlabApiReturnAllMergeRequestsNullTest() {
        initialNullCheckSetup();
        assertNull(mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
    }

    @Test
    public void gitlabApiGetDiffNullTest() {
        initialNullCheckSetup();
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void gitlabApiGetCommitsNullTest() {
        initialNullCheckSetup();
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getAllMergeRequestWithoutMemberIDTest() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(mergeRequestApi.getMergeRequestChanges(anyInt(), anyInt())).thenReturn(mergeRequests.get(0));
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        List<MergeRequestDto> expectedMergeRequestDtoList = createTestMergeRequestDto(mergeRequests, gitLabApi);
        assertEquals(expectedMergeRequestDtoList, mergeRequestDtoList);
    }

    @Test
    public void getAllMergeRequestsWithoutMemberIDTestException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
    }

    @Test
    public void getAllMergeRequestWithMemberIDTest() throws GitLabApiException {
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(mergeRequestApi.getMergeRequestChanges(anyInt(), anyInt())).thenReturn(mergeRequests.get(0));
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(notesApi.getMergeRequestNotes(projectId, mergeRequestIdA)).thenReturn(notesList);
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId);
        List<MergeRequestDto> expectedMergeRequestDtoList = createTestMergeRequestDto(mergeRequests, gitLabApi);
        assertEquals(mergeRequestDtoList, expectedMergeRequestDtoList);
    }

    @Test
    public void getAllMergeRequestsWithMemberIDTestException() throws GitLabApiException {
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
    }

    @Test
    public void getAllCommitsFromMergeRequestTest() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commits.get(0));
        List<CommitDto> commitDtoList = mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA);
        List<CommitDto> expectedCommitDtoList = CommitMock.generateTestCommitDto(commits, gitLabApi, projectId);
        assertEquals(expectedCommitDtoList, commitDtoList);
    }

    @Test
    public void getAllCommitsFromMergeRequestTestGitLabException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getAllCommitsFromMergeRequestTestDtoException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commits.get(0));
        when(commitsApi.getDiff(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTest() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequestChanges(projectId, mergeRequestIdA)).thenReturn(mergeRequest);
        when(mergeRequest.getChanges()).thenReturn(diffs);
        String mergeRequestDiff = mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA);
        String expectedMergeRequestDiff = DiffStringParser.parseDiff(diffs);
        assertEquals(expectedMergeRequestDiff, mergeRequestDiff);
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequestChanges(projectId, mergeRequestIdA)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

}