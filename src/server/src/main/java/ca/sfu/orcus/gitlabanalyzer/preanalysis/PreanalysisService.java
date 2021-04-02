package ca.sfu.orcus.gitlabanalyzer.preanalysis;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreanalysisService {
    private final PreanalysisRepository preanalysisRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public PreanalysisService(PreanalysisRepository preanalysisRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.preanalysisRepository = preanalysisRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }
}

