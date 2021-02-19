package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

import static ca.sfu.orcus.gitlabanalyzer.authentication.JwtService.JwtType;

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
            String jwt = jwtService.createJwt(newUser, JwtType.PAT);
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

    public String addNewUserByUserPass(AuthenticationUser newUser) throws IllegalArgumentException, BadRequestException {
        String user = newUser.getUsername();
        String pass = newUser.getPassword();
        if (user == null || user.equals("") || pass == null || pass.equals("")) {
            throw new BadRequestException("Username or Password are empty");
        }
        if (userPassIsValid(user, pass)) {
            try {
                GitLabApi gitLabApi = GitLabApi.oauth2Login(System.getenv("GITLAB_URL"), user, pass);
                String authToken = gitLabApi.getAuthToken();
                newUser.setAuthToken(authToken);
                String jwt = jwtService.createJwt(newUser, JwtType.USER_PASS);
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

    private boolean userPassIsValid(String user, String pass) {
        try {
            GitLabApi gitLabApi = GitLabApi.oauth2Login(System.getenv("GITLAB_URL"), user, pass);
            gitLabApi.getUserApi().getCurrentUser();
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }

    public boolean jwtIsValid(String jwt) {
        return (jwtService.jwtSignatureOk(jwt) && repository.contains(jwt) && signInSuccess(jwt));
    }

    private boolean signInSuccess(String jwt) {
        try {
            JwtType type = jwtService.getType(jwt);
            if (type == JwtType.PAT) {
                String pat = repository.getPatFor(jwt);
                getUsernameFromPat(pat);        // If we can successfully get the current user, then the pat is valid
                return true;
            } else if (type == JwtType.USER_PASS) {
                String authToken = repository.getAuthTokenFor(jwt);
                testAuthToken(authToken);
                return true;
            } else {
                return false;
            }
        } catch (GitLabApiException e) {
            return false;
        }
    }

    // If we can successfully get the current user, then the auth token is valid
    private void testAuthToken(String authToken) throws GitLabApiException {
        GitLabApi gitLabApi = new GitLabApi(System.getenv("GITLAB_URL"), Constants.TokenType.OAUTH2_ACCESS, authToken);
        gitLabApi.getUserApi().getCurrentUser();
    }

    public GitLabApi getGitLabApiFor(String jwt) {
        if (jwtIsValid(jwt)) {
            JwtType type = jwtService.getType(jwt);
            return getGitLabApiForType(jwt, type);
        } else {
            return null;
        }
    }

    private GitLabApi getGitLabApiForType(String jwt, JwtType type) {
        if (type == JwtType.PAT) {
            return getGitLabApiForPat(jwt);
        } else if (type == JwtType.USER_PASS) {
            return getGitLabApiForUserPass(jwt);
        } else {
            return null;
        }
    }

    private GitLabApi getGitLabApiForPat(String jwt) {
        try {
            String pat = repository.getPatFor(jwt);
            getUsernameFromPat(pat);
            return new GitLabApi(System.getenv("GITLAB_URL"), pat);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private GitLabApi getGitLabApiForUserPass(String jwt) {
        try {
            String authToken = repository.getAuthTokenFor(jwt);
            testAuthToken(authToken);
            return new GitLabApi("GITLAB_URL", Constants.TokenType.OAUTH2_ACCESS, authToken);
        } catch (GitLabApiException e) {
            return null;
        }
    }
}
