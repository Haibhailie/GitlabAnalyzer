package ca.sfu.orcus.gitlabanalyzer.mocks.models;

import org.gitlab4j.api.models.ProjectStatistics;

import java.util.Random;

public final class ProjectStatisticsMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    public static final long defaultCommitCount = rand.nextInt(upperBound);
    public static final long defaultStorageSize = rand.nextInt(upperBound);
    public static final long defaultRepositorySize = rand.nextInt(upperBound);
    public static final long defaultWikiSize = rand.nextInt(upperBound);
    public static final long defaultLfsObjectsSize = rand.nextInt(upperBound / 2);
    public static final long defaultJobArtifactsSize = rand.nextInt(upperBound / 2);

    public static ProjectStatistics createProjectStatistics() {
        return createProjectStatistics(defaultCommitCount,
                defaultStorageSize,
                defaultRepositorySize,
                defaultWikiSize,
                defaultLfsObjectsSize,
                defaultJobArtifactsSize);
    }

    public static ProjectStatistics createProjectStatistics(long commitCount,
                                                            long storageSize,
                                                            long repositorySize,
                                                            long wikiSize,
                                                            long lfsObjectsSize,
                                                            long jobArtifactsSize) {
        ProjectStatistics projectStatistics = new ProjectStatistics();

        projectStatistics.setCommitCount(commitCount);
        projectStatistics.setStorageSize(storageSize);
        projectStatistics.setRepositorySize(repositorySize);
        projectStatistics.setWikiSize(wikiSize);
        projectStatistics.setLfsObjectsSize(lfsObjectsSize);
        projectStatistics.setJobArtifactsSize(jobArtifactsSize);

        return projectStatistics;
    }
}
