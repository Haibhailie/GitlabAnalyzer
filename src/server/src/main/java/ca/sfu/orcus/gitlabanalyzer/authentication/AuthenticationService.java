package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationRepository repository;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(AuthenticationRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public String addNewUser(User newUser) throws IllegalArgumentException {
        if (patIsValid(newUser.getPat())) {
            String jwt = jwtService.createJwt(newUser);
            newUser.setJwt(jwt);
            repository.addNewUser(newUser);
            return jwt;
        } else {
            throw new IllegalArgumentException("Invalid pat");
        }
    }

    // try getting some small amount of data using the GitLabApi object to check if the pat token was valid
    private boolean patIsValid(String pat) {
        try {
            GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca/", pat);
            gitLabApi.getUserApi().getUser(1);
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }

    public void validateJwt(String jwt) throws IllegalArgumentException {
        if (repository.contains(jwt)) {
            String pat = repository.getPatFor(jwt);
            if (!patIsValid(pat)) {
                throw new IllegalArgumentException("Invalid pat");
            }
        } else {
            throw new IllegalArgumentException("Invalid pat");
        }
    }
}
