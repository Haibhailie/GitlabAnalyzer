package ca.sfu.orcus.gitlabanalyzer.models;

import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDiffDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.sfu.orcus.gitlabanalyzer.models.AssigneeMock.generateAssignee;

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
    public static final int numAdditions = 6;
    public static final int numDeletions = 12;
    public static final Date dateNow = new Date(); //present Date
    public static final Date dateUntil = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000); //present date + 7 days
    public static final Date dateSince = new Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000); //present date - 7 days
    public static final String title = "title";
    public static final String authorEmail = "jimcarry@carryingyou.com";
    public static final String message = "";
    public static final String sha = "123456";
    public static final String mockCodeDiff = "RandomChangesGoHereLol";
    public static CommitStats commitStats;

    public static final String jwt = "";
    public static final List<Note> notesList = new ArrayList<>();

    public static List<MergeRequest> generateTestMergeRequestList() {
        List<MergeRequest> tempMergeRequestList = new ArrayList<>();

        MergeRequest tempMergeRequestA = generateMergeRequest(generateAssignee(userId), mergeRequestIdA);
        MergeRequest tempMergeRequestB = generateMergeRequest(generateAssignee(userIdB), mergeRequestIdB);

        tempMergeRequestList.add(tempMergeRequestA);
        tempMergeRequestList.add(tempMergeRequestB);
        return tempMergeRequestList;
    }

    public static MergeRequest generateMergeRequest(Assignee assignee, int mergeRequestId){
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
        return tempMergeRequest;
    }

    public static List<Commit> generateMergeRequestTestCommitList() {
        Commit commitA = CommitMock.createCommit();
        Commit commitB = CommitMock.createCommit();
        List<Commit> generatedCommitList = new ArrayList<>();

        commitA.setId(String.valueOf(projectId));
        commitA.setTitle(title);
        commitA.setAuthorName(authorName);
        commitA.setAuthorEmail(authorEmail);
        commitA.setMessage(message);
        commitA.setId(sha);
        commitA.setCommittedDate(dateNow);
        commitA.setStats(commitStats);
        commitA.setShortId(sha);

        commitB.setId(String.valueOf(projectId));
        commitB.setTitle(title);
        commitB.setAuthorName(authorName);
        commitB.setAuthorEmail(authorEmail);
        commitB.setMessage(message);
        commitB.setId(sha);
        commitB.setCommittedDate(dateNow);
        commitB.setStats(commitStats);
        commitB.setShortId(sha);

        generatedCommitList.add(commitA);
        generatedCommitList.add(commitB);
        return generatedCommitList;
    }

    public static List<Diff> generateTestDiffList() {
        List<Diff> presentTempDiff = new ArrayList<>();

        Diff diffA = new Diff();

        diffA.setDiff(mockCodeDiff);
        diffA.setDeletedFile(false);
        diffA.setNewFile(false);
        diffA.setRenamedFile(true);
        diffA.setNewPath("Root");
        diffA.setOldPath("Not Root");

        presentTempDiff.add(diffA);
        return presentTempDiff;
    }

    public List<MergeRequestDto> generateTestMergeRequestDto(List<MergeRequest> mergeRequests, GitLabApi gitLabApi) throws GitLabApiException {
        List<MergeRequestDto> expectedMergeRequestDtoList = new ArrayList<>();
        for (MergeRequest m : mergeRequests) {
            if (m.getCreatedAt().after(dateSince) && m.getCreatedAt().before(dateUntil)) {
                expectedMergeRequestDtoList.add(new MergeRequestDto(gitLabApi, projectId, m));
            }
        }
        return expectedMergeRequestDtoList;
    }

    public List<MergeRequestDiffDto> generateMergeRequestDiffDto(List<Diff> diffs, List<Commit> commits) {
        List<MergeRequestDiffDto> expectedMergeRequestDiffDto = new ArrayList<>();
        int indexIterator = 0;
        for (Diff d : diffs) {
            expectedMergeRequestDiffDto.add(new MergeRequestDiffDto(commits.get(indexIterator), d));
            indexIterator++;
        }
        return expectedMergeRequestDiffDto;
    }

}
