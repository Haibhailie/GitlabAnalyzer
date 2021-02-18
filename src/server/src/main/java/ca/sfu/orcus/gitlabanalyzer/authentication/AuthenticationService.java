package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

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
                String jwt = jwtService.createJwt(newUser);
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

    private boolean userPassIsValid(String user, String pass) {
        try {
            GitLabApi gitLabApi = GitLabApi.oauth2Login(System.getenv("GITLAB_URL"), user, pass);
            gitLabApi.getUserApi().getCurrentUser();
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }
}
