package ca.sfu.orcus.gitlabanalyzer.preanalysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PreanalysisController {
    private final PreanalysisService preanalysisService;

    @Autowired
    public PreanalysisController(PreanalysisService preanalysisService) {
        this.preanalysisService = preanalysisService;
    }
}