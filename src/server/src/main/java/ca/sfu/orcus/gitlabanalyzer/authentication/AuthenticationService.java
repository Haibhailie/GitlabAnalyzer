package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationRepository repository;
    private final JwtTokenCreator tokenCreator;

    @Autowired
    public AuthenticationService(AuthenticationRepository repository, JwtTokenCreator tokenCreator) {
        this.repository = repository;
        this.tokenCreator = tokenCreator;
    }

    public String addNewUser(User newUser) throws IllegalArgumentException {
        if (patIsValid(newUser.getPat())) {
            String jwt = tokenCreator.createJwt(newUser);
            newUser.setJwt(jwt);
            repository.addNewUser(newUser);
            return jwt;
        } else {
            throw new IllegalArgumentException("Invalid pat");
        }
    }

    private boolean patIsValid(String pat) {
        try {
            GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca/", pat);
            gitLabApi.getProjectApi().getProjects();
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }
}
