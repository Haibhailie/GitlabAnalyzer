package ca.sfu.orcus.gitlabanalyzer.file;

import org.gitlab4j.api.models.Diff;

public class FileDto {
    String path;
    int commitId;
    int mergeRequestId;
    String diff;
    Diff gitDiff;
    boolean isIgnored;
}
