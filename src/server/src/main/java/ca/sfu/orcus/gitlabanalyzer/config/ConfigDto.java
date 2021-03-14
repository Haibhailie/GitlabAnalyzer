package ca.sfu.orcus.gitlabanalyzer.config;

import java.util.Map;

public class ConfigDto {

    private double diffAdditionMultiplier;
    private double diffDeletionMultiplier;
    private double diffCommentMultiplier;
    private double diffSpacingMultiplier;
    private double diffSyntaxChangeMultiplier;

    private static final Map<Integer, String> languageScores = Map.of();
}
