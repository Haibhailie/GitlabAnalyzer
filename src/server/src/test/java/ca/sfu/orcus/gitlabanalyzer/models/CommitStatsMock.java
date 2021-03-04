package ca.sfu.orcus.gitlabanalyzer.models;

import java.util.Random;
import org.gitlab4j.api.models.CommitStats;

public class CommitStatsMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    public static final int defaultNumAdditions = rand.nextInt(upperBound);
    public static final int defaultNumDeletions = rand.nextInt(upperBound);
    public static final int defaultNumTotal = defaultNumAdditions + defaultNumDeletions;

    public static CommitStats createCommitStats() {
        return createCommitStats(defaultNumAdditions, defaultNumDeletions, defaultNumTotal);
    }

    public static CommitStats createCommitStats(int additions, int deletions, int total) {
        CommitStats commitStats = new CommitStats();

        commitStats.setAdditions(additions);
        commitStats.setDeletions(deletions);
        commitStats.setTotal(total);

        return commitStats;
    }
}
