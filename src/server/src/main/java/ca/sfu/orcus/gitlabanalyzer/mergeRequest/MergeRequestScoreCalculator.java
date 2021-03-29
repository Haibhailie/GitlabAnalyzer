package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Arrays;
import java.util.List;

//The reason this class still exists instead of moving this single function to the DTO is because we'll be extracting data from the config here and pushing it forward for calculation
//Thought it might be better if we do that
public class MergeRequestScoreCalculator {

    //TODO: Get these from config
    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public List<FileDto> getMergeRequestScore(MergeRequest mergeRequestChanges) {

        // regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(diffsList, addLOCFactor, deleteLOCFactor, syntaxChangeFactor, blankLOCFactor, spacingChangeFactor);
    }
}
