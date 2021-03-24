package ca.sfu.orcus.gitlabanalyzer.file;

public class FileDto {
    String path;
    String commitId;
    String[] diff;
    double score;
    boolean isIgnored;
    // might want projectId as well although im not sure?

    public FileDto(String path, String commitId, String[] diff, double score) {
        this.path = path;
        this.commitId = commitId;
        this.diff = diff;
        this.score = score;
        this.isIgnored = false;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public void setDiff(String[] diff) {
        this.diff = diff;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }
}
