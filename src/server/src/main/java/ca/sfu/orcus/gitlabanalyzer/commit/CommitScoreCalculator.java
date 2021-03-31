package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

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
