package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mocks.GitLabApiMock;
import ca.sfu.orcus.gitlabanalyzer.models.MergeRequestMock;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MergeRequestServiceTest extends MergeRequestMock {

    @InjectMocks
    private MergeRequestService mergeRequestService;

    @Mock
    private GitLabApiWrapper gitLabApiWrapper;

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

    @BeforeAll
    public static void setup() {
        mergeRequests = MergeRequestMock.generateTestMergeRequestList();
        commitStats = MergeRequestMock.getTestCommitStats();
        commits = MergeRequestMock.generateMRTestCommitList();
        diffs = MergeRequestMock.generateTestDiffList();
    }

    @Test
    public void gitlabAPIPrimaryNullTest() {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(null);
        gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil));
        assertNull(mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getAllMergeRequestWithoutMemberIDTest() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        List<MergeRequestDto> expectedMergeRequestDtoList = generateTestMergeRequestDto(mergeRequests, gitLabApi);
        assertNotNull(mergeRequestDtoList);
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
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(notesApi.getMergeRequestNotes(projectId, mergeRequestIdA)).thenReturn(notesList);
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.returnAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId);
        List<MergeRequestDto> expectedMergeRequestDtoList = generateTestMergeRequestDto(mergeRequests, gitLabApi);
        assertNotNull(mergeRequestDtoList);
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
        List<CommitDto> expectedCommitDtoList = generateTestCommitDto(commits, gitLabApi);
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
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(List.of(commits.get(0)));
        when(commitsApi.getDiff(projectId, sha)).thenReturn(diffs);
        List<MergeRequestDiffDto> mergeRequestDiffDtoList = mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA);
        List<MergeRequestDiffDto> expectedMergeRequestDiffDto = generateMergeRequestDiffDto(diffs, commits);
        assertNotNull(mergeRequestDiffDtoList);
        assertEquals(expectedMergeRequestDiffDto, mergeRequestDiffDtoList);
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabCommitException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabDiffException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(List.of(commits.get(0)));
        when(commitsApi.getDiff(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

}

