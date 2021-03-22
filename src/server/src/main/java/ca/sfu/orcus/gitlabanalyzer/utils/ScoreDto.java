package ca.sfu.orcus.gitlabanalyzer.utils;

public class ScoreDto {

    String[] unifiedDiff;
    Scores score;

    public ScoreDto(String[] unifiedDiff) {
        this.unifiedDiff = unifiedDiff;
    }

    public void setScores(double totalScore, int scoreAdditions, int scoreDeletions, int scoreBlankAdditions, int scoreSyntaxChanges, int scoreSpacingChanges) {
        score = new Scores(totalScore, scoreAdditions, scoreDeletions, scoreBlankAdditions, scoreSyntaxChanges, scoreSpacingChanges);
    }
    
    class Scores {
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
    }
}
