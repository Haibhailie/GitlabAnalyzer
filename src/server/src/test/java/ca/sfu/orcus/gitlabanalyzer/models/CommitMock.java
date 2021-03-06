package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.CommitStats;
import org.gitlab4j.api.models.Diff;

import java.util.*;

public class CommitMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    public static final int defaultId = rand.nextInt(upperBound);
    public static final String defaultTitle = UUID.randomUUID().toString();
    public static final String defaultAuthor = UUID.randomUUID().toString();
    public static final String defaultEmail = UUID.randomUUID().toString();
    public static final String defaultMessage = UUID.randomUUID().toString();
    public static final String defaultSha = UUID.randomUUID().toString();
    public static final Date defaultDate = new Date();

    public static final String mockCodeDiff = "RandomChangesGoHereLol";

    public static final int defaultNumAdditions = rand.nextInt(upperBound);
    public static final int defaultNumDeletions = rand.nextInt(upperBound);
    public static final int defaultNumTotal = defaultNumAdditions + defaultNumDeletions;

    public static Commit createCommit() {
        Commit commit = new Commit();

        commit.setTitle(defaultTitle);
        commit.setAuthorName(defaultAuthor);
        commit.setAuthorEmail(defaultEmail);
        commit.setMessage(defaultMessage);
        commit.setId(defaultSha);
        commit.setCommittedDate(defaultDate);
        commit.setStats(createCommitStats());
        commit.setShortId(defaultSha);

        return commit;
    }

    public static CommitStats createCommitStats() {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(defaultNumAdditions);
        commitStats.setDeletions(defaultNumDeletions);
        commitStats.setTotal(defaultNumTotal);

        return commitStats;
    }

    public static List<Commit> createTestCommitList() {
        List<Commit> commits = new ArrayList<>();
        Commit commitA = createCommit();
        Commit commitB = createCommit();

        commits.add(commitA);
        commits.add(commitB);
        return commits;
    }

    public static List<Diff> createTestDiffList() {
        Diff diffA = new Diff();
        diffA.setDiff(mockCodeDiff);
        diffA.setDeletedFile(false);
        diffA.setNewFile(false);
        diffA.setRenamedFile(true);
        diffA.setNewPath("Root");
        diffA.setOldPath("Not Root");

        Diff diffB = new Diff();
        diffB.setDiff(mockCodeDiff);
        diffB.setDeletedFile(false);
        diffB.setNewFile(true);
        diffB.setRenamedFile(false);
        diffB.setNewPath("Root");
        diffB.setOldPath("Not Root");

        List<Diff> presentTempDiff = new ArrayList<>();
        presentTempDiff.add(diffA);
        presentTempDiff.add(diffB);

        return presentTempDiff;
    }
}
