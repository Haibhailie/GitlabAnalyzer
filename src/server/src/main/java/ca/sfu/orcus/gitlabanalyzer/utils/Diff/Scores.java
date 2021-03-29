package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

public class Scores {
    double totalScore;
    int scoreAdditions;
    int scoreDeletions;
    int scoreBlankAdditions;
    int scoreSyntaxChanges;
    int scoreSpacingChanges;

    public Scores(double totalScore, int scoreAdditions, int scoreDeletions, int scoreBlankAdditions, int scoreSyntaxChanges, int scoreSpacingChanges) {
        this.totalScore = totalScore;
        this.scoreAdditions = scoreAdditions;
        this.scoreDeletions = scoreDeletions;
        this.scoreBlankAdditions = scoreBlankAdditions;
        this.scoreSyntaxChanges = scoreSyntaxChanges;
        this.scoreSpacingChanges = scoreSpacingChanges;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Scores)) {
            return false;
        }

        Scores m = (Scores) o;

        return this.scoreAdditions == m.scoreAdditions
                && this.scoreDeletions == m.scoreDeletions
                && this.scoreBlankAdditions == m.scoreBlankAdditions
                && this.scoreSyntaxChanges == m.scoreSyntaxChanges
                && this.scoreSpacingChanges == m.scoreSpacingChanges
                && this.totalScore == m.totalScore;
    }
}
