package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    private final AnalysisRepository analysisRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public AnalysisService(AnalysisRepository analysisRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.analysisRepository = analysisRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public void analyzeProject(String jwt, int projectId) throws GitLabApiException {

    }
}

