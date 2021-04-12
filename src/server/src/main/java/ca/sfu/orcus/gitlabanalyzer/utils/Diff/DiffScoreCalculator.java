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
    private final double realCodeWrittenInALineFactor = 0.5;

    double addLOCFactor;
    double deleteLOCFactor;
    double syntaxChangeFactor;
    double blankLOCFactor;
    double spacingChangeFactor;

    private List<String> generatedDiffList = new ArrayList<>();
    List<FileDiffDto> fileDiffs = new ArrayList<>();

    private String singleLineComment;
    private String multiLineCommentStart;
    private String multiLineCommentEnd;
    private String syntaxInCode;
    private double scoreMultiplier;

    public DiffScoreDto parseDiffList(List<String> diffStrings) {
        resetCount();
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
                if (checkForCommentedLine(line)) {
                    numBlankAdditions++;
                    numLineAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_BLANK));
                } else if (line.substring(1).replaceAll("\\s+", "").startsWith(multiLineCommentStart)) {
                    handleMultiLineComments(lineNumber);
                } else if (checkForSyntaxOnlyLine(line)) {
                    numSyntaxChanges++;
                    numLineAdditions++;
                } else if (line.substring(1).replaceAll("\\s+", "").length() > 0) {
                    numLineAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION));
                } else {
                    numBlankAdditions++;
                    numLineAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_BLANK));
                }
            } else if (line.startsWith("-")) {
                if (checkSpacingChanges(lineNumber, line)) {
                    numLineAdditions++;
                    numLineDeletions++;
                    continue;
                    //Log spacing changed line
                } else if (checkSyntaxChanges(lineNumber, line)) {
                    numLineAdditions++;
                    numLineDeletions++;
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

    private void resetCount() {
        numLineAdditions = 0;
        numLineDeletions = 0;
        numBlankAdditions = 0;
        numSyntaxChanges = 0;
        numSpacingChanges = 0;
    }

    private boolean checkForCommentedLine(String line) {

        return line.substring(1).replaceAll("\\s+", "").startsWith(singleLineComment);
    }

    private boolean checkForSyntaxOnlyLine(String line) {
        String[] syntaxArray = syntaxInCode.split("\\s+");
        for (String syntax : syntaxArray) {
            if (line.substring(1).replaceAll("\\s+", "").equals(syntax)) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_SYNTAX));
                return true;
            }
        }
        return false;
    }

    private void handleMultiLineComments(int lineNumber) {
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (presentLine.startsWith("+") && !presentLine.contains(multiLineCommentEnd)) {
                numBlankAdditions++;
                fileDiffs.add(new FileDiffDto(presentLine, FileDiffDto.DiffLineType.ADDITION_BLANK));
                generatedDiffList.set(i, "---REMOVED");
            } else if ((presentLine.startsWith("+") && presentLine.contains(multiLineCommentEnd))) {
                numBlankAdditions++;
                fileDiffs.add(new FileDiffDto(presentLine, FileDiffDto.DiffLineType.ADDITION_BLANK));
                generatedDiffList.set(i, "---REMOVED");
                break;
            } else {
                numLineDeletions++;
                fileDiffs.add(new FileDiffDto(presentLine, FileDiffDto.DiffLineType.DELETION_BLANK));
                generatedDiffList.set(i, "---REMOVED");
            }
        }
    }

    private boolean checkSyntaxChanges(int lineNumber, String testingLine) {
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (presentLine.startsWith("+")) {
                //Checking the level of similarity between the two lines (if difference > half the original line, then
                //it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, presentLine).length() > (testingLine.length()) * realCodeWrittenInALineFactor) {
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

    //Loop that separates the diffs and scores of individual files in a Merge Request diff
    public List<FileDto> fileScoreCalculator(String jwt, ConfigService configService, List<String> diffsList) {

        setMultipliersFromConfig(jwt, configService);
        List<FileDto> fileDtos = new ArrayList<>();
        List<DiffScoreDto> diffScoreDtos = new ArrayList<>();
        List<Double> fileSpecificScoreMultiplier = new ArrayList<>();

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
            String fileName = getFileNameFromDiff(fileDiffs);
            fileDtos.add(new FileDto(fileName));

            //setSyntaxFromConfig(jwt, configService, ".cpp");
            setSyntaxFromConfig(jwt, configService, getFileTypeFromDiff(fileName));

            fileSpecificScoreMultiplier.add(scoreMultiplier);
            diffScoreDtos.add(generateDiffScoreDto(fileDiffs));
        }

        for (int i = 0; i < diffScoreDtos.size(); i++) {

            DiffScoreDto presentDiffScore = diffScoreDtos.get(i);
            int additions = presentDiffScore.getNumLineAdditions();
            int deletions = presentDiffScore.getNumLineDeletions();
            int blankAdditions = presentDiffScore.getNumBlankAdditions();
            int syntaxChanges = presentDiffScore.getNumSyntaxChanges();
            int spacingChanges = presentDiffScore.getNumSpacingChanges();

            double totalScore = (additions * addLOCFactor)
                    + deletions * deleteLOCFactor
                    + blankAdditions * blankLOCFactor
                    + syntaxChanges * syntaxChangeFactor
                    + spacingChanges * spacingChangeFactor;

            totalScore = totalScore * fileSpecificScoreMultiplier.get(i);

            fileDtos.get(i).setTotalScore(new Scores(totalScore,
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

            fileDtos.get(i).setFileDiffDtos(presentDiffScore.getFileDiffs(fileDiffLines.get(i), fileDiffLines.get(i + 1)));
        }
        return fileDtos;
    }

    private void setSyntaxFromConfig(String jwt, ConfigService configService, String extension) {
        try {
            Optional<ConfigDto> configDto = configService.getCurrentConfig(jwt);
            if (configDto.isPresent()) {
                setSyntaxValues(configDto.get(), extension);
            } else {
                setDefaultSyntaxValues();
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
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
        } catch (GitLabApiException | IllegalStateException | NullPointerException e) {
            // default multipliers
            addLOCFactor = 1;
            deleteLOCFactor = 0.2;
            syntaxChangeFactor = 0.2;
            blankLOCFactor = 0;
            spacingChangeFactor = 0;
        }
    }

    private void setSyntaxValues(ConfigDto configDto, String extension) {
        setDefaultSyntaxValues();
        List<ConfigDto.FileTypeScoreDto> fileTypeScores;
        fileTypeScores = configDto.getFileScores();
        for (ConfigDto.FileTypeScoreDto f : fileTypeScores) {
            if (f.getFileExtension().equals(extension)) {
                if (!isNullOrEmpty(f.getSingleLineComment())) {
                    singleLineComment = f.getSingleLineComment();
                } else {
                    singleLineComment = "//";
                }

                if (!isNullOrEmpty(f.getMultiLineCommentStart())) {
                    multiLineCommentStart = f.getMultiLineCommentStart();
                } else {
                    multiLineCommentStart = "/*";
                }

                if (!isNullOrEmpty(f.getMultiLineCommentEnd())) {
                    multiLineCommentEnd = f.getMultiLineCommentEnd();
                } else {
                    multiLineCommentEnd = "*/";
                }

                if (!isNullOrEmpty(f.getSyntaxInCode())) {
                    syntaxInCode = f.getSyntaxInCode();
                } else {
                    syntaxInCode = "{ } [ ] ( ) & | + - / * , ! = % ` ~";
                }

                if (f.getScoreMultiplier() != 0) {
                    scoreMultiplier = f.getScoreMultiplier();
                } else {
                    scoreMultiplier = 1;
                }
            }
        }
    }

    private void setDefaultSyntaxValues() {
        singleLineComment = "//";
        multiLineCommentStart = "/*";
        multiLineCommentEnd = "*/";
        syntaxInCode = "{ } [ ] ( ) & | + - / * , ! = % ` ~";
        scoreMultiplier = 1;
    }

    private String getFileNameFromDiff(List<String> diff) {
        for (String s : diff) {
            if (s.startsWith("+++")) {
                return s.substring(s.indexOf("b/") + 2);
            }
        }
        return "N/A";
    }

    private String getFileTypeFromDiff(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.indexOf("."));
        }
        return "N/A";
    }

    private DiffScoreDto generateDiffScoreDto(List<String> diffList) {
        return parseDiffList(diffList);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
