package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

//The reason this class still exists instead of moving this single function to the DTO is because we'll be extracting data from the config here and pushing it forward for calculation
//Thought it might be better if we do that
public class CommitScoreCalculator {
    // TODO: Should be getting these from config
    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    public List<FileDto> getCommitScore(List<Diff> diffs) {

        // regex to split lines by new line and store in generatedDiffList
        String[] diffArray = DiffStringParser.parseDiff(diffs).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffArray);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(diffsList,
                addLOCFactor,
                deleteLOCFactor,
                syntaxChangeFactor,
                blankLOCFactor,
                spacingChangeFactor);

    }
}
