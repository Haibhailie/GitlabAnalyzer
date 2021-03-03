package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

import static ca.sfu.orcus.gitlabanalyzer.authentication.JwtService.JwtType;

@Service
public class AuthenticationService {
    private final AuthenticationRepository repository;
    private final JwtService jwtService;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public AuthenticationService(AuthenticationRepository repository, JwtService jwtService, GitLabApiWrapper gitLabApiWrapper) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public String registerNewPat(AuthenticationUser newUser) throws IllegalArgumentException, BadRequestException {
        String username = getUsername(newUser);
        newUser.setUsername(username);
        String jwt = jwtService.createJwt(newUser, JwtType.PAT);
        newUser.setJwt(jwt);
        repository.addNewUser(newUser);
        return jwt;
    }

    private String getUsername(AuthenticationUser newUser) throws IllegalArgumentException, BadRequestException {
        String pat = newUser.getPat();
        if (pat == null || pat.equals("")) {
            throw new BadRequestException("Pat is empty");
        }
        try {
            return gitLabApiWrapper.getUsernameFromPat(pat);
        } catch (GitLabApiException e) {
            throw new IllegalArgumentException("Pat Authentication failed");
        }
    }

    public String registerNewUserPass(AuthenticationUser newUser) throws IllegalArgumentException, BadRequestException {
        String authToken = getAuthToken(newUser.getUsername(), newUser.getPassword());
        newUser.setAuthToken(authToken);
        String jwt = jwtService.createJwt(newUser, JwtType.USER_PASS);
        newUser.setJwt(jwt);
        repository.addNewUserByUserPass(newUser);
        return jwt;
    }

    private String getAuthToken(String username, String password) throws IllegalArgumentException, BadRequestException {
        if (username == null || username.equals("") || password == null || password.equals("")) {
            throw new BadRequestException("Username or Password are empty");
        }
        Optional<String> authToken = gitLabApiWrapper.getOAuth2AuthToken(username, password);
        if (authToken.isEmpty()) {
            throw new IllegalArgumentException(("Username and password do not match"));
        }
        return authToken.get();
    }

    public boolean jwtIsValid(String jwt) {
        return (jwtService.jwtIsValid(jwt) && repository.contains(jwt) && gitLabApiWrapper.canSignIn(jwt));
    }

}
