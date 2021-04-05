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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof LOCDto)) {
            return false;
        }

        LOCDto m = (LOCDto) o;

        return this.numAdditions == m.numAdditions
                && this.numDeletions == m.numDeletions
                && this.numBlankAdditions == m.numBlankAdditions
                && this.numSyntaxChanges == m.numSyntaxChanges
                && this.numSpacingChanges == m.numSpacingChanges;
    }
}
