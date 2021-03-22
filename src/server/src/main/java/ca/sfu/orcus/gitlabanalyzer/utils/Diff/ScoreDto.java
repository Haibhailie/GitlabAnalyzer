package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

public class ScoreDto {

    String[] unifiedDiff;
    double totalScore;
    int scoreAdditions;
    int scoreDeletions;
    int scoreBlankAdditions;
    int scoreSyntaxChanges;
    int scoreSpacingChanges;

    public ScoreDto(String[] unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    public void setScores(double totalScore, int scoreAdditions, int scoreDeletions, int scoreBlankAdditions, int scoreSyntaxChanges, int scoreSpacingChanges) {
        this.totalScore = totalScore;
        this.scoreAdditions = scoreAdditions;
        this.scoreDeletions = scoreDeletions;
        this.scoreBlankAdditions = scoreBlankAdditions;
        this.scoreSyntaxChanges = scoreSyntaxChanges;
        this.scoreSpacingChanges = scoreSpacingChanges;
    }


}
