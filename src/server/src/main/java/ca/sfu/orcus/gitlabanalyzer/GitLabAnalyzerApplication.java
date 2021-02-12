package ca.sfu.orcus.gitlabanalyzer;

import ca.sfu.orcus.gitlabanalyzer.authentication.JwtTokenCreator;
import ca.sfu.orcus.gitlabanalyzer.authentication.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitLabAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitLabAnalyzerApplication.class, args);
    }

}
