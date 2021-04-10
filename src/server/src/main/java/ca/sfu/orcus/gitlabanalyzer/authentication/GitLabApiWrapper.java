package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitLabApiWrapper {

    private final JwtService jwtService;
    private final AuthenticationRepository authRepository;

    public GitLabApiWrapper(JwtService jwtService, AuthenticationRepository authRepository) {
        this.jwtService = jwtService;
        this.authRepository = authRepository;
    }

    public GitLabApi getGitLabApiFor(String jwt) {
        if (jwtService.jwtIsValid(jwt)) {
            JwtService.JwtType type = jwtService.getType(jwt);
            return getGitLabApiForType(jwt, type);
        } else {
            return null;
        }
    }

    private GitLabApi getGitLabApiForType(String jwt, JwtService.JwtType type) {
        if (type == JwtService.JwtType.PAT) {
            return getGitLabApiForPat(jwt);
        } else {
            return getGitLabApiForUserPass(jwt);
        }
    }

    private GitLabApi getGitLabApiForPat(String jwt) {
        try {
            String pat = authRepository.getPatFor(jwt);
            getUsernameFromPat(pat);
            return new GitLabApi(VariableDecoderUtil.decode("GITLAB_URL"), pat);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    private GitLabApi getGitLabApiForUserPass(String jwt) {
        try {
            String authToken = authRepository.getAuthTokenFor(jwt);
            testAuthToken(authToken);
            return new GitLabApi(VariableDecoderUtil.decode("GITLAB_URL"), Constants.TokenType.OAUTH2_ACCESS, authToken);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public String getUsernameFromPat(String pat) throws GitLabApiException {
        GitLabApi gitLabApi = new GitLabApi(VariableDecoderUtil.decode("GITLAB_URL"), pat);
        User currentUser = gitLabApi.getUserApi().getCurrentUser();
        return currentUser.getUsername();
    }

    // Expects a validated jwt token
    public boolean canSignIn(String jwt) {
        try {
            trySigningIntoGitLab(jwt);
            return true;
        } catch (GitLabApiException e) {
            return false;
        }
    }

    private void trySigningIntoGitLab(String jwt) throws GitLabApiException {
        JwtService.JwtType type = jwtService.getType(jwt);
        if (type == JwtService.JwtType.PAT) {
            String pat = authRepository.getPatFor(jwt);
            getUsernameFromPat(pat);        // If we can successfully get the current user, then the pat is valid
        } else {
            String authToken = authRepository.getAuthTokenFor(jwt);
            testAuthToken(authToken);
        }
    }

    // If we can successfully get the current user, then the auth token is valid
    private void testAuthToken(String authToken) throws GitLabApiException {
        GitLabApi gitLabApi = new GitLabApi(VariableDecoderUtil.decode("GITLAB_URL"), Constants.TokenType.OAUTH2_ACCESS, authToken);
        gitLabApi.getUserApi().getCurrentUser();
    }

    public Optional<String> getOAuth2AuthToken(String username, String password) {
        try {
            // If oauth2Login fails then the username or password are invalid
            GitLabApi gitLabApi = GitLabApi.oauth2Login(VariableDecoderUtil.decode("GITLAB_URL"), username, password);
            String authToken = gitLabApi.getAuthToken();
            return Optional.of(authToken);
        } catch (GitLabApiException e) {
            return Optional.empty();
        }
    }

    public int getGitLabUserIdFromJwt(String jwt) throws GitLabApiException {
        GitLabApi gitLabApi = getGitLabApiFor(jwt);
        return gitLabApi.getUserApi().getCurrentUser().getId();
    }

    public Optional<String> getProjectUrl(String jwt, int projectId) {
        GitLabApi gitLabApi = getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return Optional.empty();
        }

        try {
            Project project = gitLabApi.getProjectApi().getProject(projectId);
            return Optional.of(project.getWebUrl());
        } catch (GitLabApiException e) {
            return Optional.empty();
        }
    }
}
