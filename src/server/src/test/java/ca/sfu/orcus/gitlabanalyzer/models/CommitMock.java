package ca.sfu.orcus.gitlabanalyzer.models;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
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

        return createCommit(String.valueOf(defaultId), defaultTitle, defaultAuthor, defaultEmail, defaultMessage, defaultSha, defaultDate, createCommitStats(), defaultSha);

    }

    public static Commit createCommit(String projectId, String title, String authorName, String authorEmail, String message, String sha, Date date, CommitStats commitStats, String shortId) {
        Commit commit = new Commit();

        commit.setId(String.valueOf(projectId));
        commit.setTitle(title);
        commit.setAuthorName(authorName);
        commit.setAuthorEmail(authorEmail);
        commit.setMessage(message);
        commit.setId(sha);
        commit.setCommittedDate(date);
        commit.setStats(commitStats);
        commit.setShortId(shortId);

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
        Diff diffA = DiffMock.createTestDiff(mockCodeDiff, false, false, true, "Root", "Not Root");
        Diff diffB = DiffMock.createTestDiff(mockCodeDiff, false, true, false, "Root", "Not Root");

        List<Diff> presentTempDiff = new ArrayList<>();
        presentTempDiff.add(diffA);
        presentTempDiff.add(diffB);

        return presentTempDiff;
    }

    public static List<CommitDto> generateTestCommitDto(List<Commit> commits, GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        List<CommitDto> expectedCommitDtoList = new ArrayList<>();
        for (Commit c : commits) {
            expectedCommitDtoList.add(new CommitDto(gitLabApi, projectId, c));
        }
        return expectedCommitDtoList;
    }
}
