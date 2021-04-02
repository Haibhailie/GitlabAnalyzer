package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationRepository;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.authentication.JwtService;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto.GeneralTypeScoreDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigRepository;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.*;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MergeRequestScoreCalculator {

    //TODO: Get these from config
    double addLOCFactor = 1;
    double deleteLOCFactor = 0.2;
    double syntaxChangeFactor = 0.2;
    double blankLOCFactor = 0;
    double spacingChangeFactor = 0;

    private JwtService jwtService = new JwtService();
    private AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    private ConfigRepository configRepository = new ConfigRepository();
    private GitLabApiWrapper gitLabApiWrapper = new GitLabApiWrapper(jwtService, authenticationRepository);
    private ConfigService configService = new ConfigService(configRepository, gitLabApiWrapper);

    public List<FileDto> getMergeRequestScore(String jwt, MergeRequest mergeRequestChanges) throws GitLabApiException {
        try {
            Optional<ConfigDto> configDto = configService.getCurrentConfig(jwt);
            if (configDto.isPresent()) {
                List<GeneralTypeScoreDto> list = configDto.get().getGeneralScores();
                for (GeneralTypeScoreDto g : list) {
                    switch (g.getType()) {
                        case "addLoc" -> addLOCFactor = g.getValue();
                        case "deleteLoc" -> deleteLOCFactor = g.getValue();
                        case "Syntax" -> syntaxChangeFactor = g.getValue();
                        case "blank" -> blankLOCFactor = g.getValue();
                        case "spacing" -> spacingChangeFactor = g.getValue();
                        default -> throw new IllegalStateException("Unexpected value: " + g.getType());
                    }
                }
            }
        } catch (GitLabApiException e) {
            // default multipliers
        }
        // regex to split lines by new line and store in generatedDiffList
        String[] diffString = DiffStringParser.parseDiff(mergeRequestChanges.getChanges()).split("\\r?\\n");
        List<String> diffsList = Arrays.asList(diffString);

        DiffScoreCalculator diffScoreCalculator = new DiffScoreCalculator();
        return diffScoreCalculator.fileScoreCalculator(diffsList, addLOCFactor, deleteLOCFactor, syntaxChangeFactor, blankLOCFactor, spacingChangeFactor);
    }
}
