package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

public class DiffScoreDto {
    int numLineAdditions;
    int numLineDeletions;
    int numBlankAdditions;
    int numSyntaxChanges;
    int numSpacingChanges;

    public DiffScoreDto(int numLineAdditions, int numLineDeletions, int numBlankAdditions, int numSyntaxChanges, int numSpacingChanges) {
        this.numLineAdditions = numLineAdditions;
        this.numLineDeletions = numLineDeletions;
        this.numBlankAdditions = numBlankAdditions;
        this.numSyntaxChanges = numSyntaxChanges;
        this.numSpacingChanges = numSpacingChanges;
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

}
