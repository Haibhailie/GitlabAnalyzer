package ca.sfu.orcus.gitlabanalyzer.utils.Diff;

import ca.sfu.orcus.gitlabanalyzer.file.FileDiffDto;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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
                if (line.substring(1).replaceAll("\\s+", "").length() > 0) {
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

    //Loop that separates the diffs and scores of individual files in a Merge Request diff
    public List<FileDto> fileScoreCalculator(List<String> diffsList,
                                             double addFactor,
                                             double deleteFactor,
                                             double syntaxFactor,
                                             double blankFactor,
                                             double spacingFactor) {
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

            DiffScoreDto presentDiffScore = diffScoreDtos.get(i);
            int additions = presentDiffScore.getNumLineAdditions();
            int deletions = presentDiffScore.getNumLineDeletions();
            int blankAdditions = presentDiffScore.getNumBlankAdditions();
            int syntaxChanges = presentDiffScore.getNumSyntaxChanges();
            int spacingChanges = presentDiffScore.getNumSpacingChanges();

            double totalScore = (additions * addFactor)
                    + deletions * deleteFactor
                    + blankAdditions * blankFactor
                    + syntaxChanges * syntaxFactor
                    + spacingChanges * spacingFactor;

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
