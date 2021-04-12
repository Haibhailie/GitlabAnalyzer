package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberMock;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId));
    }

    @Test
    public void getAllMergeRequestsWithoutMemberIDTestException() throws GitLabApiException {
        initialMergeRequestTestSetup();
        when(mergeRequestApi.getMergeRequests(projectId, Constants.MergeRequestState.MERGED)).thenThrow(GitLabApiException.class);
        assertNull(mergeRequestService.getAllMergeRequests(jwt, projectId));
    }

}