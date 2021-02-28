package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;

public class MergeRequestDiffDto {
    private boolean isNewFile;
    private boolean isDeletedFile;
    private boolean isRenamedFile;
    private String commitName;
    private String newPath;
    private String oldPath;
    private String diff;

    public MergeRequestDiffDto(Commit presentCommit, Diff presentDiff) {
        setCommitName(presentCommit.getTitle());
        setDiff(presentDiff.getDiff());
        setDeletedFile(presentDiff.getDeletedFile());
        setNewFile(presentDiff.getNewFile());
        setNewPath(presentDiff.getNewPath());
        setOldPath(presentDiff.getOldPath());
        setRenamedFile(presentDiff.getRenamedFile());
    }

    public void setCommitName(String commitName) {
        this.commitName = commitName;
    }

    public void setNewFile(boolean newFile) {
        isNewFile = newFile;
    }

    public void setDeletedFile(boolean deletedFile) {
        isDeletedFile = deletedFile;
    }

    public void setRenamedFile(boolean renamedFile) {
        isRenamedFile = renamedFile;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }
}
