package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiffScoreDto {

    int numLineAdditions;
    int numLineDeletions;
    int numBlankAdditions;
    int numSyntaxChanges;
    int numSpacingChanges;
    List<FileDiffDto> fileDiffs;

    public DiffScoreDto(int numLineAdditions, int numLineDeletions, int numBlankAdditions, int numSyntaxChanges, int numSpacingChanges, List<FileDiffDto> fileDiffs) {
        this.numLineAdditions = numLineAdditions;
        this.numLineDeletions = numLineDeletions;
        this.numBlankAdditions = numBlankAdditions;
        this.numSyntaxChanges = numSyntaxChanges;
        this.numSpacingChanges = numSpacingChanges;
        this.fileDiffs = fileDiffs;
    }

    public int getNumLineAdditions() {
        return numLineAdditions;
    }

    public int getNumLineDeletions() {
        return numLineDeletions;
    }

    public int getNumBlankAdditions() {
        return numBlankAdditions;
    }

    public int getNumSyntaxChanges() {
        return numSyntaxChanges;
    }

    public int getNumSpacingChanges() {
        return numSpacingChanges;
    }

    public List<FileDiffDto> getFileDiffs() {
        return fileDiffs;
    }

}
