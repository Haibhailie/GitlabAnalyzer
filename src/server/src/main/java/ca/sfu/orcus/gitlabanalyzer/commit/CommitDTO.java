package ca.sfu.orcus.gitlabanalyzer.commit;

import org.gitlab4j.api.models.Diff;

import java.util.Date;
import java.util.List;

public class CommitDTO {
    private String title;
    private String author;
    private String id;
    private Date dateCommited;
    private String getMessage;
    private int numAdditions;
    private int numDeletions;
    private int total;
    private List<Diff> diffs;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateCommited(Date dateCommited) {
        this.dateCommited = dateCommited;
    }

    public void setGetMessage(String getMessage) {
        this.getMessage = getMessage;
    }

    public void setNumAdditions(int numAdditions) {
        this.numAdditions = numAdditions;
    }

    public void setNumDeletions(int numDeletions) {
        this.numDeletions = numDeletions;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setDiffs(List<Diff> diffs) {
        this.diffs = diffs;
    }
}
