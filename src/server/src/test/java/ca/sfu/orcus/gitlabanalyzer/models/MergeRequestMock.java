package ca.sfu.orcus.gitlabanalyzer.models;

import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.sfu.orcus.gitlabanalyzer.models.AssigneeMock.createAssignee;

public class MergeRequestMock extends AuthorMock {
    public static final int projectId = 10;
    public static final int mergeRequestIdA = 9;
    public static final int mergeRequestIdB = 10;
    public static final boolean hasConflicts = false;
    public static final int userId = 6;
    public static final int userIdB = 7;
    public static final String description = "Random Description";
    public static final String sourceBranch = "Testing";
    public static final String targetBranch = "master";
    public static final Date dateNow = new Date(); //present Date
    public static final Date dateUntil = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000); //present date + 7 days
    public static final Date dateSince = new Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000); //present date - 7 days
    public static final String title = "title";
    public static final String authorEmail = "jimcarry@carryingyou.com";
    public static final String message = "";
    public static final String sha = "123456";
    public static CommitStats commitStats;

    public static final String jwt = "";
    public static final List<Note> notesList = new ArrayList<>();

    public static List<MergeRequest> createTestMergeRequestList() {
        List<MergeRequest> tempMergeRequestList = new ArrayList<>();

        MergeRequest tempMergeRequestA = createMergeRequest(createAssignee(userId), mergeRequestIdA);
        MergeRequest tempMergeRequestB = createMergeRequest(createAssignee(userIdB), mergeRequestIdB);

        tempMergeRequestList.add(tempMergeRequestA);
        tempMergeRequestList.add(tempMergeRequestB);
        return tempMergeRequestList;
    }

    public static MergeRequest createMergeRequest(Assignee assignee, int mergeRequestId) {
        MergeRequest tempMergeRequest = new MergeRequest();
        tempMergeRequest.setAuthor(generateAuthor());
        tempMergeRequest.setIid(mergeRequestId);
        tempMergeRequest.setHasConflicts(hasConflicts);
        tempMergeRequest.setState("opened");
        tempMergeRequest.setAssignee(assignee);
        tempMergeRequest.setDescription(description);
        tempMergeRequest.setSourceBranch(sourceBranch);
        tempMergeRequest.setTargetBranch(targetBranch);
        tempMergeRequest.setCreatedAt(dateNow);
        tempMergeRequest.setMergedAt(dateNow);
        tempMergeRequest.setTitle(title);
        tempMergeRequest.setChanges(DiffMock.createTestDiffList());
        return tempMergeRequest;
    }

    public static List<Commit> createMergeRequestTestCommitList() {
        Commit commitA = CommitMock.createCommit(String.valueOf(projectId), title, defaultAuthorName, authorEmail, message, sha, dateNow, commitStats, sha);
        Commit commitB = CommitMock.createCommit(String.valueOf(projectId), title, defaultAuthorName, authorEmail, message, sha, dateNow, commitStats, sha);
        List<Commit> generatedCommitList = new ArrayList<>();

        generatedCommitList.add(commitA);
        generatedCommitList.add(commitB);
        return generatedCommitList;
    }

    public List<MergeRequestDto> createTestMergeRequestDto(List<MergeRequest> mergeRequests, GitLabApi gitLabApi) throws GitLabApiException {
        List<MergeRequestDto> expectedMergeRequestDtoList = new ArrayList<>();
        for (MergeRequest m : mergeRequests) {
            if (m.getCreatedAt().after(dateSince) && m.getCreatedAt().before(dateUntil)) {
                expectedMergeRequestDtoList.add(new MergeRequestDto(jwt, gitLabApi, projectId, m));
            }
        }
        return expectedMergeRequestDtoList;
    }

}
