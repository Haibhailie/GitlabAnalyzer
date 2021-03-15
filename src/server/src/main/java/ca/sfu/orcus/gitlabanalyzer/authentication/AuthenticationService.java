package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

import static ca.sfu.orcus.gitlabanalyzer.authentication.JwtService.JwtType;

@Service
public class AuthenticationService {
    private final AuthenticationRepository authRepository;
    private final JwtService jwtService;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public AuthenticationService(AuthenticationRepository authRepository, JwtService jwtService, GitLabApiWrapper gitLabApiWrapper) {
        this.authRepository = authRepository;
        this.jwtService = jwtService;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public String signInWithPat(AuthenticationUser newUser) throws BadRequestException, IllegalArgumentException {
        String username = getUsername(newUser);
        if (authRepository.containsPat(newUser.getPat())) {
            return authRepository.getJwtForPat(newUser.getPat());
        }
        newUser.setUsername(username);
        String jwt = jwtService.createJwt(newUser, JwtType.PAT);
        newUser.setJwt(jwt);
        authRepository.addNewUserByPat(newUser);
        return jwt;
    }

    private String getUsername(AuthenticationUser newUser) throws BadRequestException, IllegalArgumentException {
        String pat = newUser.getPat();
        if (pat == null) {
            throw new BadRequestException("Pat is empty");
        }
        try {
            return gitLabApiWrapper.getUsernameFromPat(pat);
        } catch (GitLabApiException e) {
            throw new IllegalArgumentException("Pat Authentication failed");
        }
    }

    public String signInWithUserPass(AuthenticationUser newUser) throws BadRequestException, IllegalArgumentException {
        String authToken = getAuthToken(newUser.getUsername(), newUser.getPassword());
        if (authRepository.containsAuthToken(authToken)) {
            return authRepository.getJwtForAuthToken(authToken);
        }
        newUser.setAuthToken(authToken);
        String jwt = jwtService.createJwt(newUser, JwtType.USER_PASS);
        newUser.setJwt(jwt);
        authRepository.addNewUserByUserPass(newUser);
        return jwt;
    }

    private String getAuthToken(String username, String password) throws BadRequestException, IllegalArgumentException {
        if (username == null || password == null) {
            throw new BadRequestException("Username or Password are empty");
        }
        Optional<String> authToken = gitLabApiWrapper.getOAuth2AuthToken(username, password);
        if (authToken.isEmpty()) {
            throw new IllegalArgumentException(("Username and password do not match"));
        }
        return authToken.get();
    }

    public boolean jwtIsValid(String jwt) {
        return (jwtService.jwtIsValid(jwt) && authRepository.contains(jwt) && gitLabApiWrapper.canSignIn(jwt));
    }

}
