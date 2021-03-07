package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Diff;

import java.util.ArrayList;
import java.util.List;

public class DiffMock {

    public static final String mockCodeDiff = "RandomChangesGoHereLol";
    public static final String newPath = "Root";
    public static final String oldPath = "Not Root";

    public static List<Diff> createTestDiffList() {

        List<Diff> presentTempDiff = new ArrayList<>();
        presentTempDiff.add(createTestDiff(mockCodeDiff, false, false, true, newPath, oldPath));
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
