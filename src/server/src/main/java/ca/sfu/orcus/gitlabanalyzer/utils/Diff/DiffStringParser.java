package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;
import org.gitlab4j.api.models.Diff;

import java.io.File;
import java.util.ArrayList;
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

    public static List<String> parseDiffList(List<Diff> diffsList) {
        List<String> diffListString = new ArrayList<>();
        for (Diff presentDiff : diffsList) {
            StringBuilder presentDiffString = new StringBuilder();
            setDiffPaths(presentDiff);
            String header = "--- a/" + oldPath + "\n+++ b/" + newPath + "\n";
            String diffBody = presentDiff.getDiff();
            presentDiffString.append(header).append(diffBody);
            diffListString.add(presentDiffString.toString());
        }
        return diffListString;
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
