package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MergeRequestMock {
    public static final int projectId = 10;
    public static final int mergeRequestIdA = 9;
    public static final int mergeRequestIdB = 10;
    public static final boolean hasConflicts = false;
    public static final int userId = 6;
    public static final int userIdB = 7;
    public static final String assignedTo = "John";
    public static final String author = "John";
    public static final String description = "Random Description";
    public static final String sourceBranch = "Testing";
    public static final String targetBranch = "master";
    public static final int numAdditions = 6;
    public static final int numDeletions = 12;
    public static final Date dateNow = new Date();
    public static final Date dateUntil = new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000);
    public static final String title = "title";
    public static final String authorEmail = "jimcarry@carryingyou.com";
    public static final String message = "";
    public static final String sha = "123456";
    public static final String mockCodeDiff = "RandomChangesGoHereLol";
    public static CommitStats commitStats;

    public static final String jwt = "";
    public static final Date dateSince = new Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000);
    public static final List<Note> notesList = new ArrayList<>();

    public static List<MergeRequest> generateTestMergeRequestList() {
        List<MergeRequest> tempMergeRequestList = new ArrayList<>();
        MergeRequest tempMergeRequestA = new MergeRequest();
        MergeRequest tempMergeRequestB = new MergeRequest();

        Author tempAuthorA = new Author();
        tempAuthorA.setName(author);
        tempAuthorA.setId(userId);
        tempMergeRequestA.setAuthor(tempAuthorA);
        tempMergeRequestA.setIid(mergeRequestIdA);
        tempMergeRequestA.setHasConflicts(hasConflicts);
        tempMergeRequestA.setState("opened");
        Assignee tempAssigneeA = new Assignee();
        tempAssigneeA.setName(assignedTo);
        tempAssigneeA.setId(userId);
        tempMergeRequestA.setAssignee(tempAssigneeA);
        tempMergeRequestA.setDescription(description);
        tempMergeRequestA.setSourceBranch(sourceBranch);
        tempMergeRequestA.setTargetBranch(targetBranch);
        tempMergeRequestA.setCreatedAt(dateNow);
        tempMergeRequestA.setMergedAt(dateNow);

        Author tempAuthorB = new Author();
        tempAuthorB.setName(author);
        tempAuthorB.setId(userIdB);
        tempMergeRequestB.setAuthor(tempAuthorB);
        tempMergeRequestB.setIid(mergeRequestIdB);
        tempMergeRequestB.setHasConflicts(hasConflicts);
        tempMergeRequestB.setState("opened");
        Assignee tempAssigneeB = new Assignee();
        tempAssigneeB.setName(assignedTo);
        tempAssigneeB.setId(userId);
        tempMergeRequestB.setAssignee(tempAssigneeB);
        tempMergeRequestB.setDescription(description);
        tempMergeRequestB.setSourceBranch(sourceBranch);
        tempMergeRequestB.setTargetBranch(targetBranch);
        tempMergeRequestB.setCreatedAt(dateUntil);
        tempMergeRequestB.setMergedAt(dateUntil);

        tempMergeRequestList.add(tempMergeRequestA);
        tempMergeRequestList.add(tempMergeRequestB);

        return tempMergeRequestList;
    }

    public static List<Commit> generateMRTestCommitList() {


        Commit commitA = new Commit();
        Commit commitB = new Commit();
        List<Commit> generatedCommitList = new ArrayList<>();

        commitA.setId(String.valueOf(projectId));
        commitA.setTitle(title);
        commitA.setAuthorName(author);
        commitA.setAuthorEmail(authorEmail);
        commitA.setMessage(message);
        commitA.setId(sha);
        commitA.setCommittedDate(dateNow);
        commitA.setStats(commitStats);
        commitA.setShortId(sha);

        commitB.setId(String.valueOf(projectId));
        commitB.setTitle(title);
        commitB.setAuthorName(author);
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

    public static CommitStats getTestCommitStats() {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(numAdditions);
        commitStats.setDeletions(numDeletions);
        commitStats.setTotal(numAdditions + numDeletions);

        return commitStats;
    }

    public static List<Diff> generateTestDiffList() {
        List<Diff> presentTempDiff = new ArrayList<>();

        Diff diffA = new Diff();
        Diff diffB = new Diff();

        diffA.setDiff(mockCodeDiff);
        diffA.setDeletedFile(false);
        diffA.setNewFile(false);
        diffA.setRenamedFile(true);
        diffA.setNewPath("Root");
        diffA.setOldPath("Not Root");

        diffB.setDiff(mockCodeDiff);
        diffB.setDeletedFile(false);
        diffB.setNewFile(true);
        diffB.setRenamedFile(false);
        diffB.setNewPath("Root");
        diffB.setOldPath("Not Root");

        presentTempDiff.add(diffA);
        presentTempDiff.add(diffB);

        return presentTempDiff;
    }
}
