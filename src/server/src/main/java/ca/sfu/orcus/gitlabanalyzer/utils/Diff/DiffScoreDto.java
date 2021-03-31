package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;

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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof DiffScoreDto)) {
            return false;
        }

        DiffScoreDto m = (DiffScoreDto) o;

        return (this.fileDiffs.equals(m.fileDiffs)
                && this.numLineAdditions == m.numLineAdditions
                && this.numBlankAdditions == m.numBlankAdditions
                && this.numLineDeletions == m.numLineDeletions
                && this.numSpacingChanges == m.numSpacingChanges
                && this.numSyntaxChanges == m.numSyntaxChanges);
    }
}
