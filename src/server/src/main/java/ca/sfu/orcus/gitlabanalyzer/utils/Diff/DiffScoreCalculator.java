package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffScoreCalculator {

    private int numLineAdditions = 0;
    private int numLineDeletions = 0;
    private int numBlankAdditions = 0;
    private int numSyntaxChanges = 0;
    private int numSpacingChanges = 0;
    private final double lineSimilarityFactor = 0.5;
    private List<String> generatedDiffList = new ArrayList<>();
    List<FileDiffDto> fileDiffs = new ArrayList<>();

    public DiffScoreDto parseDiffList(List<String> diffStrings) {
        generatedDiffList = diffStrings;

        int lineNumber = -1;
        for (String line : generatedDiffList) {
            lineNumber++;
            if (line.startsWith("---")) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.HEADER));
                //Log line skipped
            } else if (line.startsWith("+++")) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.HEADER));
                //Log line skipped
            } else if (line.startsWith("@@")) {
                fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.LINE_NUMBER_SPECIFICATION));
                //Log line skipped
            } else if (line.startsWith("+")) {
                if (line.substring(1).length() > 0) {
                    numLineAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION));
                } else {
                    numBlankAdditions++;
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_BLANK));
                }
            } else if (line.startsWith("-")) {
                if (checkSyntaxChanges(lineNumber, line)) {
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_SYNTAX));
                    break;
                }
                if (checkSpacingChanges(lineNumber, line)) {
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_SPACING));
                    break;
                }
                if (checkAddedBlankSpaces(lineNumber, line)) {
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.ADDITION_BLANK));
                    break;
                } else {
                    fileDiffs.add(new FileDiffDto(line, FileDiffDto.DiffLineType.DELETION));
                    numLineDeletions++;
                }
            }
        }
        return new DiffScoreDto(numLineAdditions, numLineDeletions, numBlankAdditions, numSyntaxChanges, numSpacingChanges, fileDiffs);
    }

    private boolean checkSyntaxChanges(int lineNumber, String testingLine) {
        int presentLine = 0;
        for (String line : generatedDiffList) {
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking the level of similarity between the two lines (if difference > half the original line, then it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, line).length() > (testingLine.length()) * lineSimilarityFactor) {
                    numSyntaxChanges++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
            presentLine++;
        }
        return false;
    }

    private boolean checkAddedBlankSpaces(int lineNumber, String testingLine) {
        int presentLine = 0;
        for (String line : generatedDiffList) {
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking whether all the differences between two lines are just blank spaces
                if (StringUtils.difference(testingLine, line).isBlank()) {
                    numBlankAdditions++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
            presentLine++;
        }
        return false;
    }

    private boolean checkSpacingChanges(int lineNumber, String testingLine) {
        int presentLine = 0;
        for (String line : generatedDiffList) {
            if (presentLine < lineNumber) {
                continue;
            }
            if (line.startsWith("-")) {
                continue;
            } else {
                //Checking if the line has only spacing changes (basically removing all spaces and checking if the lines are still the same)
                if (testingLine.replaceAll("\\s+", "").equalsIgnoreCase(line.replaceAll("\\s+", ""))) {
                    numSpacingChanges++;
                    generatedDiffList.set(presentLine, "---");
                    return true;
                }
            }
            presentLine++;
        }
        return false;
    }

    //Loop that separates the diffs and scores of individual files in a Merge Request diff
    public List<FileDto> fileScoreCalculator(List<String> diffsList, double addFactor, double deleteFactor, double syntaxFactor, double blankFactor, double spacingFactor) {
        List<FileDto> fileDtos = new ArrayList<>();
        List<DiffScoreDto> diffScoreDtos = new ArrayList<>();
        for (int i = 0; i < diffsList.size(); i++) {
            if (diffsList.get(i).startsWith("diff --")) {
                for (int j = i + 1; j < diffsList.size(); j++) {
                    if (diffsList.get(j).startsWith("diff --")) {
                        fileDtos.add(new FileDto(convertToString(diffsList.subList(i, j - 1)), getFileNameFromDiff((diffsList.subList(i, j - 1)))));
                        diffScoreDtos.add(generateDiffScoreDto(diffsList.subList(i, j - 1)));
                        break;
                    } else if (j + 1 == diffsList.size()) {
                        fileDtos.add(new FileDto(convertToString(diffsList.subList(i, j - 1)), getFileNameFromDiff((diffsList.subList(i, j - 1)))));
                        diffScoreDtos.add(generateDiffScoreDto(diffsList.subList(i, j - 1)));
                    }
                }
            }
        }

        for (int i = 0; i < diffScoreDtos.size(); i++) {
            double totalScore = (diffScoreDtos.get(i).getNumLineAdditions() * addFactor)
                    + (diffScoreDtos.get(i).getNumLineDeletions() * deleteFactor)
                    + (diffScoreDtos.get(i).getNumBlankAdditions() * blankFactor)
                    + (diffScoreDtos.get(i).getNumSyntaxChanges() * syntaxFactor)
                    + (diffScoreDtos.get(i).getNumSpacingChanges() * spacingFactor);

            fileDtos.get(i).setMergeRequestFileScore(new Scores(totalScore,
                    diffScoreDtos.get(i).getNumLineAdditions(),
                    diffScoreDtos.get(i).getNumLineDeletions(),
                    diffScoreDtos.get(i).getNumBlankAdditions(),
                    diffScoreDtos.get(i).getNumSyntaxChanges(),
                    diffScoreDtos.get(i).getNumSpacingChanges()));

            fileDtos.get(i).setLinesOfCodeChanges(new LOCDto(diffScoreDtos.get(i).getNumLineAdditions(),
                    (diffScoreDtos.get(i).getNumLineDeletions()),
                    (diffScoreDtos.get(i).getNumBlankAdditions()),
                    (diffScoreDtos.get(i).getNumSyntaxChanges()),
                    (diffScoreDtos.get(i).getNumSpacingChanges())));

            fileDtos.get(i).setFileDiffDtos(diffScoreDtos.get(i).getFileDiffs());

        }
        return fileDtos;
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

    private String[] convertToString(List<String> stringList) {
        Object[] objectList = stringList.toArray();
        return Arrays.copyOf(objectList, objectList.length, String[].class);
    }
}
