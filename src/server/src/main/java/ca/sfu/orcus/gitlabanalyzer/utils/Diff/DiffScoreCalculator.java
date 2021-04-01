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
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (!presentLine.startsWith("-")) {
                //Checking the level of similarity between the two lines (if difference > half the original line, then
                //it's considered a new addition, else a syntax change)
                if (StringUtils.difference(testingLine, presentLine).length() > (testingLine.length()) * lineSimilarityFactor) {
                    numSyntaxChanges++;
                    generatedDiffList.set(i, "---");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSpacingChanges(int lineNumber, String testingLine) {
        for (int i = lineNumber; i < generatedDiffList.size(); i++) {
            String presentLine = generatedDiffList.get(i);
            if (!presentLine.startsWith("-")) {
                //Checks if the difference between two lines is just blank spaces/spacing changes
                if (StringUtils.difference(testingLine, presentLine).isBlank()) {
                    numSpacingChanges++;
                    generatedDiffList.set(i, "---");
                    return true;
                }
            }
        }
        return false;
    }

    private int findDiffStartIndex(List<String> diffsList, int startIndex) {
        for (int i = startIndex; i < diffsList.size(); i++) {
            if (diffsList.get(i).startsWith("diff --")) {
                return i;
            }
        }

        return diffsList.size();
    }

    //Loop that separates the diffs and scores of individual files in a Merge Request diff
    public List<FileDto> fileScoreCalculator(List<String> diffsList, double addFactor, double deleteFactor, double syntaxFactor, double blankFactor, double spacingFactor) {
        List<FileDto> fileDtos = new ArrayList<>();
        List<DiffScoreDto> diffScoreDtos = new ArrayList<>();
        int diffStart = findDiffStartIndex(diffsList, 0);
        while (diffStart < diffsList.size()) {
            int nextDiffStart = findDiffStartIndex(diffsList, diffStart + 1);
            List<String> diffList = diffsList.subList(diffStart, nextDiffStart - 1);

            fileDtos.add(new FileDto(convertToString(diffList), getFileNameFromDiff(diffList)));
            diffScoreDtos.add(generateDiffScoreDto(diffList));

            diffStart = nextDiffStart;
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
