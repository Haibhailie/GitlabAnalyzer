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

    public String addNewUserFromPat(AuthenticationUser newUser) throws IllegalArgumentException {
        try {
            String pat = newUser.getPat();
            newUser.setUsername(getUsernameFromPat(pat));
            String jwt = jwtService.createJwt(newUser);
            newUser.setJwt(jwt);
            repository.addNewUser(newUser);
            return jwt;
        } catch (GitLabApiException e) {
            throw new IllegalArgumentException("Pat Authentication failed");
        }
    }

    private String getUsernameFromPat(String pat) throws GitLabApiException {
        GitLabApi gitLabApi = new GitLabApi("http://cmpt373-1211-09.cmpt.sfu.ca/", pat);
        org.gitlab4j.api.models.User currentUser = gitLabApi.getUserApi().getCurrentUser();
        return currentUser.getUsername();
    }

    public boolean jwtIsValid(String jwt) {
        return (jwtService.jwtSignatureOk(jwt) && repository.contains(jwt) && signInSuccess(jwt));
    }

    private boolean signInSuccess(String jwt) {
        try {
            String pat = repository.getPatFor(jwt);
            getUsernameFromPat(pat); // If we can successfully get the current user, then the pat is valid
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }
}
