package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectStatistics;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public final class ProjectMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    public static final int defaultId = rand.nextInt(upperBound);
    public static final String defaultName = UUID.randomUUID().toString();
    public static final Date defaultCreatedAt = new Date();
    public static final Date defaultLastActivityAt = new Date();
    public static final String defaultDefaultBranch = "master";

    public static Project createProject(ProjectStatistics projectStatistics) {
        return createProject(defaultId,
                defaultName,
                projectStatistics,
                defaultCreatedAt,
                defaultLastActivityAt,
                defaultDefaultBranch);
    }

    public static Project createProject(int id,
                                        String name,
                                        ProjectStatistics statistics,
                                        Date createdAt,
                                        Date lastActivityAt,
                                        String defaultBranch) {
        Project project = new Project();

        project.setId(id);
        project.setName(name);
        project.setStatistics(statistics);
        project.setCreatedAt(createdAt);
        project.setLastActivityAt(lastActivityAt);
        project.setDefaultBranch(defaultBranch);

        return project;
    }
}
