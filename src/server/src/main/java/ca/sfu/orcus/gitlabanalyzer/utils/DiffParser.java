package ca.sfu.orcus.gitlabanalyzer.utils;

import org.gitlab4j.api.models.Diff;

import java.util.List;

public class DiffParser {

    public static String parseDiff(List<Diff> diffsList){
        StringBuilder convertedDiff = new StringBuilder();
        for(Diff presentDiff:diffsList){
            String headerA = "diff --git a/"+presentDiff.getOldPath()+" b/"+presentDiff.getNewPath()+"\n";
            String headerB = "--- a/"+presentDiff.getOldPath()+"\n+++ b/"+presentDiff.getOldPath()+"\n";
            String diffBody = presentDiff.getDiff();
            convertedDiff.append(headerA).append(headerB).append(diffBody);
        }
        return convertedDiff.toString();
    }

}
