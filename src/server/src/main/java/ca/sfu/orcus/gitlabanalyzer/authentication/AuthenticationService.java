package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

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

    public String addNewUserFromPat(AuthenticationUser newUser) throws IllegalArgumentException, BadRequestException {
        String pat = newUser.getPat();
        if (pat == null || pat.equals("")) {
            throw new BadRequestException("Pat is empty");
        }
        try {
            newUser.setUsername(gitLabApiWrapper.getUsernameFromPat(pat));
            String jwt = jwtService.createJwt(newUser, JwtType.PAT);
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
                GitLabApi gitLabApi = GitLabApi.oauth2Login(VariableDecoderUtil.decode("GITLAB_URL"), user, pass);
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
            GitLabApi gitLabApi = GitLabApi.oauth2Login(VariableDecoderUtil.decode("GITLAB_URL"), user, pass);
            gitLabApi.getUserApi().getCurrentUser();
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }

    public boolean jwtIsValid(String jwt) {
        return (jwtService.jwtIsValid(jwt) && repository.contains(jwt) && gitLabApiWrapper.canSignIn(jwt));
    }

}
