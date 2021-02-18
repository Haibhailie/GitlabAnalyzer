package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

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

    public String addNewUserByUserPass(User newUser) throws IllegalArgumentException, BadRequestException {
        String user = newUser.getUsername();
        String pass = newUser.getPassword();
        if (user == null || user.equals("") || pass == null || pass.equals("")) {
            throw new BadRequestException("Username or Password are empty");
        }
        if (userPassIsValid(user, pass)) {
            try {
                GitLabApi gitLabApi = GitLabApi.oauth2Login("http://cmpt373-1211-09.cmpt.sfu.ca/", user, pass);
                String authToken = gitLabApi.getAuthToken();
                newUser.setAuthToken(authToken);
                String jwt = tokenCreator.createJwt(newUser);
                newUser.setJwt(jwt);
                repository.addNewUserByUserPass(newUser);
                return jwt;
            } catch (GitLabApiException e) {
                throw new IllegalArgumentException(("Username and password do not match"));
            }
        } else {
            throw new IllegalArgumentException("Username and password do not match");
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

    private boolean userPassIsValid(String user, String pass) {
        try {
            GitLabApi gitLabApi = GitLabApi.oauth2Login("http://cmpt373-1211-09.cmpt.sfu.ca/", user, pass);
            gitLabApi.getUserApi().getCurrentUser();
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }
}
