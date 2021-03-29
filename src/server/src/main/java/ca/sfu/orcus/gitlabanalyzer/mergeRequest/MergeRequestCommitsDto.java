package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;

public class MergeRequestCommitsDto {
    Scores fileScore;
    LOCDto linesOfCodeChanges;

    public MergeRequestCommitsDto(Scores fileScore, LOCDto linesOfCodeChanges) {
        this.fileScore = fileScore;
        this.linesOfCodeChanges = linesOfCodeChanges;
    }

}
