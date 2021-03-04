package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.ProjectStatistics;

import java.util.Random;

public final class ProjectStatisticsMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    public static final long defaultCommitCount = rand.nextInt(upperBound);
    public static final long defaultRepositorySize = rand.nextInt(upperBound);

    public static ProjectStatistics createProjectStatistics() {
        return createProjectStatistics(defaultCommitCount, defaultRepositorySize);
    }

    public static ProjectStatistics createProjectStatistics(long commitCount, long repositorySize) {
        ProjectStatistics projectStatistics = new ProjectStatistics();

        projectStatistics.setCommitCount(commitCount);
        projectStatistics.setRepositorySize(repositorySize);

        return projectStatistics;
    }
}
