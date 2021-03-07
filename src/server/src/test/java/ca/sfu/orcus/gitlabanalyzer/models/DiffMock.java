package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Diff;

import java.util.ArrayList;
import java.util.List;

public class DiffMock {

    public static final String defaultMockCodeDiff = "RandomChangesGoHereLol";
    public static final String defaultNewPath = "Root";
    public static final String defaultOldPath = "Not Root";

    public static List<Diff> createTestDiffList() {

        List<Diff> presentTempDiff = new ArrayList<>();
        presentTempDiff.add(createTestDiff(defaultMockCodeDiff, false, false, true, defaultNewPath, defaultOldPath));
        return presentTempDiff;
    }

    public static Diff createTestDiff(String mockCodeDiff, boolean isDeleted, boolean isNew, boolean isRenamed, String newPath, String oldPath) {
        Diff diff = new Diff();
        diff.setDiff(mockCodeDiff);
        diff.setDeletedFile(isDeleted);
        diff.setNewFile(isNew);
        diff.setRenamedFile(isRenamed);
        diff.setNewPath(newPath);
        diff.setOldPath(oldPath);
        return diff;
    }

}
