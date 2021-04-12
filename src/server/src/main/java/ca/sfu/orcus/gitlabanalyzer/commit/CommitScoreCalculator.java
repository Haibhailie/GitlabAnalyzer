package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.DiffStringParser;
import org.gitlab4j.api.models.Diff;

import java.util.Arrays;
import java.util.List;

public class CommitScoreCalculator {
    private final ConfigService configService;

    public CommitScoreCalculator(ConfigService configService) {
        this.configService = configService;
    }

    public List<FileDto> getCommitScore(String jwt, List<Diff> diffs) {
        // regex to split lines by new line and store in generatedDiffList
        String[] diffArray = DiffStringParser.parseDiff(diffs).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffArray);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(currentConfig, diffsList);
    }
}
