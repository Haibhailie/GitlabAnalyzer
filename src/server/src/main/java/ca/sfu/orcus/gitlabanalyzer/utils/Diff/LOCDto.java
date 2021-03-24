package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

public class LOCDto {
    int numAdditions;
    int numDeletions;
    int numBlankAdditions;
    int numSyntaxChanges;
    int numSpacingChanges;

    public LOCDto(int numAdditions, int numDeletions, int numBlankAdditions, int numSyntaxChanges, int numSpacingChanges) {
        this.numAdditions = numAdditions;
        this.numDeletions = numDeletions;
        this.numBlankAdditions = numBlankAdditions;
        this.numSyntaxChanges = numSyntaxChanges;
        this.numSpacingChanges = numSpacingChanges;
    }
}
