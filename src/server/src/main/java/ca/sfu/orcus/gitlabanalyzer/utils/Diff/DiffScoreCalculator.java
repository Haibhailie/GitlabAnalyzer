package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiffScoreCalculator {

    private int numLineAdditions = 0;
    private int numLineDeletions = 0;
    private int numBlankAdditions = 0;
    private int numSyntaxChanges = 0;
    private int numSpacingChanges = 0;

    double addLOCFactor;
    double deleteLOCFactor;
    double syntaxChangeFactor;
    double blankLOCFactor;
    double spacingChangeFactor;

    private final double lineSimilarityFactor = 0.5;

    private List<String> generatedDiffList = new ArrayList<>();
    List<FileDiffDto> fileDiffs = new ArrayList<>();

    public DiffScoreDto parseDiffList(List<String> diffStrings) {
        generatedDiffList = diffStrings;
        int lineNumber = -1;

        for (String line : generatedDiffList) {
            lineNumber++;
            if (line.startsWith("---REMOVED")) {
                //Log already checked line
            } else if (line.startsWith("---")
                    || line.startsWith("+++")
                    || line.startsWith("diff --git")) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.HEADER));
            } else if (line.startsWith("@@")) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.HEADER));
            } else if (line.startsWith("+")) {
                if (line.substring(1).length() > 0) {
                    numLineAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION));
                } else {
                    numBlankAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_BLANK));
                }
            } else if (line.startsWith("-")) {
                if (checkSpacingChanges(lineNumber, line)) {
                    continue;
                    //Log spacing changed line
                } else if (checkSyntaxChanges(lineNumber, line)) {
                    continue;
                    //Log syntax changed line
                } else {
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.DELETION));
                    numLineDeletions++;
                }
            } else {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.UNCHANGED));
            }
        }
        return new DiffScoreDto(numLineAdditions, numLineDeletions, numBlankAdditions, numSyntaxChanges, numSpacingChanges, fileDiffs);
    }

    private boolean checkSyntaxChanges(int lineNumber, String testingLine) {
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (presentLine.startsWith("+")) {
                //Checking the level of similarity between the two lines (if difference > half the original line, then
                //it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, presentLine).length() > (testingLine.length()) * lineSimilarityFactor) {
                    numSyntaxChanges++;
                    fileDiffs.add(new FileDiffDto(testingLine, FileDiffDto.DiffLineType.DELETION_SYNTAX));
                    fileDiffs.add(new FileDiffDto(presentLine, FileDiffDto.DiffLineType.ADDITION_SYNTAX));
                    generatedDiffList.set(i, "---REMOVED");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSpacingChanges(int lineNumber, String testingLine) {
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (presentLine.startsWith("+")) {
                //Checks if the difference between two lines is just blank spaces/spacing changes
                if (checkStringDifferencesForBlankSpace(testingLine.substring(1), presentLine.substring(1))) {
                    numSpacingChanges++;
                    fileDiffs.add(new FileDiffDto(testingLine, FileDiffDto.DiffLineType.DELETION_BLANK));
                    fileDiffs.add(new FileDiffDto(presentLine, FileDiffDto.DiffLineType.ADDITION_BLANK));
                    generatedDiffList.set(i, "---REMOVED");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkStringDifferencesForBlankSpace(String str1, String str2) {
        return str1.replaceAll("\\s+", "").trim().equals(str2.replaceAll("\\s+", "").trim());
    }

    private int findNextFileInDiff(List<String> diffsList, int startIndex) {
        for (int i = startIndex; i < diffsList.size(); i++) {
            if (diffsList.get(i).startsWith("diff --")) {
                return i;
            }
        }
        return diffsList.size();
    }

    //Loop that separates the diffs and scores of individual files in a Merge Request diff
    public List<FileDto> fileScoreCalculator(String jwt, ConfigService configService, List<String> diffsList) {
        setMultipliersFromConfig(jwt, configService);
        List<FileDto> fileDtos = new ArrayList<>();
        List<DiffScoreDto> diffScoreDtos = new ArrayList<>();

        int fileCount = 0;
        List<Integer> fileDiffLines = new ArrayList<>();
        for (int i = 0; i < diffsList.size(); i++) {
            if (diffsList.get(i).startsWith("diff --")) {
                fileCount++;
                fileDiffLines.add(i);
            }
            if (diffsList.get(i).contains("No newline at end of file")) {
                diffsList.set(i, "");
            }
        }
        fileDiffLines.add(diffsList.size());

        for (int i = 0; i < fileCount; i++) {
            List<String> fileDiffs = diffsList.subList(fileDiffLines.get(i), fileDiffLines.get(i + 1));
            fileDtos.add(new FileDto(getFileNameFromDiff(fileDiffs)));
            diffScoreDtos.add(generateDiffScoreDto(fileDiffs));
        }

        for (int i = 0; i < diffScoreDtos.size(); i++) {
            int additions = diffScoreDtos.get(i).getNumLineAdditions();
            int deletions = diffScoreDtos.get(i).getNumLineDeletions();
            int blankAdditions = diffScoreDtos.get(i).getNumBlankAdditions();
            int syntaxChanges = diffScoreDtos.get(i).getNumSyntaxChanges();
            int spacingChanges = diffScoreDtos.get(i).getNumSpacingChanges();

            double totalScore = (additions * addLOCFactor)
                    + deletions * deleteLOCFactor
                    + blankAdditions * blankLOCFactor
                    + syntaxChanges * syntaxChangeFactor
                    + spacingChanges * spacingChangeFactor;

            fileDtos.get(i).setMergeRequestFileScore(new Scores(totalScore,
                    additions,
                    deletions,
                    blankAdditions,
                    syntaxChanges,
                    spacingChanges));

            fileDtos.get(i).setLinesOfCodeChanges(new LOCDto(additions,
                    deletions,
                    blankAdditions,
                    syntaxChanges,
                    spacingChanges));

            fileDtos.get(i).setFileDiffDtos(diffScoreDtos.get(i).getFileDiffs(fileDiffLines.get(i), fileDiffLines.get(i + 1)));
        }
        return fileDtos;
    }

    private void setMultipliersFromConfig(String jwt, ConfigService configService) {
        try {
            Optional<ConfigDto> configDto = configService.getCurrentConfig(jwt);
            if (configDto.isPresent()) {
                List<ConfigDto.GeneralTypeScoreDto> list = configDto.get().getGeneralScores();
                for (ConfigDto.GeneralTypeScoreDto g : list) {
                    switch (g.getType()) {
                      case ADD_FACTOR -> addLOCFactor = g.getValue();
                      case DELETE_FACTOR -> deleteLOCFactor = g.getValue();
                      case SYNTAX_FACTOR -> syntaxChangeFactor = g.getValue();
                      case BLANK_FACTOR -> blankLOCFactor = g.getValue();
                      case SPACING_FACTOR -> spacingChangeFactor = g.getValue();
                      default -> throw new IllegalStateException("Unexpected type: " + g.getType());
                    }
                }
            }
        } catch (GitLabApiException | IllegalStateException e) {
            // default multipliers
            addLOCFactor = 1;
            deleteLOCFactor = 0.2;
            syntaxChangeFactor = 0.2;
            blankLOCFactor = 0;
            spacingChangeFactor = 0;
        }
    }

    private String getFileNameFromDiff(List<String> diff) {
        for (String s : diff) {
            if (s.startsWith("+++")) {
                return s.substring(s.indexOf("b/") + 2);
            }
        }
        return "N/A";
    }

    private DiffScoreDto generateDiffScoreDto(List<String> diffList) {
        return parseDiffList(diffList);
    }

}
