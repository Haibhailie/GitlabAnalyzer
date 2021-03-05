package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.models.MergeRequestMock;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MergeRequestServiceTest extends MergeRequestMock {

    @InjectMocks
    private MergeRequestService mergeRequestService;

    @Mock
    private MergeRequestRepository mergeRequestRepository;
    @Mock
    private GitLabApiWrapper gitLabApiWrapper;
    @Mock
    private GitLabApi gitLabApi;
    @Mock
    private MergeRequestApi mergeRequestApi;
    @Mock
    private CommitsApi commitsApi;
    @Mock
    private NotesApi notesApi;

    static List<MergeRequest> mergeRequests;
    static List<Commit> commits;
    static List<Diff> diffs;

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
        assertNull(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getAllMergeRequestWithoutMemberIDTest() throws GitLabApiException {

        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);

        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil);

        List<MergeRequestDto> expectedMergeRequestDtoList = new ArrayList<>();
        for (MergeRequest m : mergeRequests) {
            if (m.getCreatedAt().after(dateSince) && m.getCreatedAt().before(dateUntil))
                expectedMergeRequestDtoList.add(new MergeRequestDto(gitLabApi, projectId, m));
        }
        assertNotNull(mergeRequestDtoList);
        assertEquals(expectedMergeRequestDtoList.size(), mergeRequestDtoList.size());
        assertEquals(expectedMergeRequestDtoList, mergeRequestDtoList);
    }

    @Test
    public void getAllMergeRequestsWithoutMemberIDTestNotNull() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(jwt, projectId, dateSince, dateUntil);
        assertNotNull(mergeRequestDtoList);
    }

    @Test
    public void getAllMergeRequestsWithoutMemberIDTestException() throws GitLabApiException {
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil));
    }

    @Test
    public void getAllMergeRequestWithMemberIDTest() throws GitLabApiException {

        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(gitLabApi.getNotesApi()).thenReturn(notesApi);
        when(notesApi.getMergeRequestNotes(projectId, mergeRequestIdA)).thenReturn(notesList);
        List<MergeRequestDto> mergeRequestDtoList = mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId);
        List<MergeRequestDto> expectedMergeRequestDtoList = new ArrayList<>();
        for (MergeRequest m : mergeRequests) {
            if (m.getAuthor().getId() == userId)
                expectedMergeRequestDtoList.add(new MergeRequestDto(gitLabApi, projectId, m));
        }
        assertNotNull(mergeRequestDtoList);
        assertEquals(expectedMergeRequestDtoList.size(), mergeRequestDtoList.size());
        assertEquals(mergeRequestDtoList, expectedMergeRequestDtoList);
    }

    @Test
    public void getAllMergeRequestsWithMemberIDTestException() throws GitLabApiException {
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllMergeRequests(gitLabApi, projectId, dateSince, dateUntil, userId));
    }

    @Test
    public void getAllCommitsFromMergeRequestTest() throws GitLabApiException {

        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commits.get(0));
        List<CommitDto> commitDtoList = mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA);
        List<CommitDto> expectedCommitDtoList = new ArrayList<>();
        for (Commit c : commits) {
            expectedCommitDtoList.add(new CommitDto(gitLabApi, projectId, c));
        }
        assertNotNull(commitDtoList);
        assertEquals(expectedCommitDtoList.size(), commitDtoList.size());
        assertEquals(expectedCommitDtoList, commitDtoList);
    }

    @Test
    public void getAllCommitsFromMergeRequestTestGitLabException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getAllCommitsFromMergeRequestTestDtoException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(commits);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(commitsApi.getCommit(projectId, sha)).thenReturn(commits.get(0));
        when(commitsApi.getDiff(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllCommitsFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTest() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(List.of(commits.get(0)));
        when(commitsApi.getDiff(projectId, sha)).thenReturn(diffs);
        List<MergeRequestDiffDto> mergeRequestDiffDtoList = mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA);
        List<MergeRequestDiffDto> expectedMergeRequestDiffDto = new ArrayList<>();
        int indexIterator = 0;
        for (Diff d : diffs) {
            expectedMergeRequestDiffDto.add(new MergeRequestDiffDto(commits.get(indexIterator), d));
            indexIterator++;
        }
        assertNotNull(mergeRequestDiffDtoList);
        assertEquals(expectedMergeRequestDiffDto.size(), mergeRequestDiffDtoList.size());
        assertEquals(expectedMergeRequestDiffDto, mergeRequestDiffDtoList);
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabCommitException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

    @Test
    public void getDiffFromMergeRequestTestGitLabDiffException() throws GitLabApiException {
        when(gitLabApiWrapper.getGitLabApiFor(jwt)).thenReturn(gitLabApi);
        when(gitLabApi.getMergeRequestApi()).thenReturn(mergeRequestApi);
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenReturn(mergeRequests);
        when(gitLabApi.getCommitsApi()).thenReturn(commitsApi);
        when(mergeRequestApi.getCommits(projectId, mergeRequestIdA)).thenReturn(List.of(commits.get(0)));
        when(commitsApi.getDiff(projectId, sha)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getDiffFromMergeRequest(jwt, projectId, mergeRequestIdA));
    }

}

