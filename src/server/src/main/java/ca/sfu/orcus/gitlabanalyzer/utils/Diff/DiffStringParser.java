package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import org.gitlab4j.api.models.Diff;

import java.util.List;

public final class DiffStringParser {

    private static String oldPath;
    private static String newPath;
    private static final String defaultPath = "dev/null";

    public static String parseDiff(List<Diff> diffsList) {
        StringBuilder convertedDiff = new StringBuilder();
        for (Diff presentDiff : diffsList) {
            setDiffPaths(presentDiff);
            String headerA = "diff --git a/" + oldPath + " b/" + newPath + "\n";
            String headerB = "--- a/" + oldPath + "\n+++ b/" + newPath + "\n";
            String diffBody = presentDiff.getDiff();
            convertedDiff.append(headerA).append(headerB).append(diffBody);
        }
        return convertedDiff.toString();
    }

    private static void setDiffPaths(Diff diff) {
        if (diff.getNewFile()) {
            oldPath = defaultPath;
            newPath = diff.getNewPath();
        } else if (diff.getDeletedFile()) {
            oldPath = diff.getOldPath();
            newPath = defaultPath;
        } else {
            oldPath = diff.getOldPath();
            newPath = diff.getNewPath();
        }
    }
}
