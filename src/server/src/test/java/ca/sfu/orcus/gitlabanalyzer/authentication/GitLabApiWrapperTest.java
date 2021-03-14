package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.UserApi;
import org.gitlab4j.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitLabApiWrapperTest {
    @Mock JwtService jwtService;
    @Mock AuthenticationRepository authRepository;
    @Mock UserApi userApi;
    @Mock GitLabApi gitLabApi;

    @InjectMocks
    GitLabApiWrapper gitLabApiWrapper;

    private final String sampleJwt = "sampleJwt";
    private final String sampleUsername = "sampleUsername";
    private final String samplePassword = "samplePassword";
    private final User sampleUser = new User();

    @Test
    public void failGetGitLabApiForInvalidJwt() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(false);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Test
    public void failGetGitLabApiForInvalidPat() throws GitLabApiException {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        setupForSignInWithPatJwt();
        assertNull(getGitLabApiWhereExceptionIsThrown());
    }

    @Test
    public void failGetGitLabApiForInvalidUserPass() throws GitLabApiException {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        setupForSignInWithUserPassJwt();
        assertNull(getGitLabApiWhereExceptionIsThrown());
    }

    private GitLabApi getGitLabApiWhereExceptionIsThrown() throws GitLabApiException {
        try (MockedConstruction<GitLabApi> ignore = mockConstruction(GitLabApi.class,
                (mock, context) -> when(mock.getUserApi()).thenReturn(userApi))) {
            when(userApi.getCurrentUser()).thenThrow(GitLabApiException.class);
            return gitLabApiWrapper.getGitLabApiFor(sampleJwt);
        }
    }

    @Test
    public void failGitLabSignInForBadPatJwt() throws GitLabApiException {
        setupForSignInWithPatJwt();
        assertFalse(tryToSignInWhenGitLabApiThrowsException());
    }

    @Test
    public void failGitLabSignInForBadUserPassJwt() throws GitLabApiException {
        setupForSignInWithUserPassJwt();
        assertFalse(tryToSignInWhenGitLabApiThrowsException());
    }

    @Test
    public void successfullySignInWithPatJwt() throws GitLabApiException {
        setupForSignInWithPatJwt();
        assertTrue(tryToSignInWhenNoExceptionsAreThrown());
    }

    @Test
    public void successfullySignInWithUserPassJwt() throws GitLabApiException {
        setupForSignInWithUserPassJwt();
        assertTrue(tryToSignInWhenNoExceptionsAreThrown());
    }

    @Test
    public void failGettingOAuth2TokenForBadUserPass() {
        try (MockedStatic<GitLabApi> gitLabApiMockedStatic = mockStatic(GitLabApi.class)) {
            gitLabApiMockedStatic.when(() ->
                    GitLabApi.oauth2Login(anyString(), anyString(), anyString())).thenThrow(GitLabApiException.class);
            Optional<String> authToken = gitLabApiWrapper.getOAuth2AuthToken(sampleUsername, samplePassword);
            assertEquals(Optional.empty(), authToken);
        }
    }

    @Test
    public void successfullyGetOAuth2Token() {
        String sampleAuthToken = "sampleAuthToken";
        try (MockedStatic<GitLabApi> gitLabApiMockedStatic = mockStatic(GitLabApi.class)) {
            gitLabApiMockedStatic.when(() -> GitLabApi.oauth2Login(anyString(), anyString(), anyString())).thenReturn(gitLabApi);
            when(gitLabApi.getAuthToken()).thenReturn(sampleAuthToken);
            Optional<String> returnedAuthToken = gitLabApiWrapper.getOAuth2AuthToken(sampleUsername, samplePassword);
            Optional<String> expectedAuthToken = Optional.of(sampleAuthToken);
            assertEquals(expectedAuthToken, returnedAuthToken);
        }
    }

    private void setupForSignInWithPatJwt() {
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.PAT);
        String samplePat = "samplePat";
        when(authRepository.getPatFor(sampleJwt)).thenReturn(samplePat);
    }

    private void setupForSignInWithUserPassJwt() {
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.USER_PASS);
        String sampleAuthToken = "sampleAuthToken";
        when(authRepository.getAuthTokenFor(sampleJwt)).thenReturn(sampleAuthToken);
    }

    private boolean tryToSignInWhenGitLabApiThrowsException() throws GitLabApiException {
        try (MockedConstruction<GitLabApi> ignore = mockConstruction(GitLabApi.class,
                (mock, context) -> when(mock.getUserApi()).thenReturn(userApi))) {
            when(userApi.getCurrentUser()).thenThrow(GitLabApiException.class);
            return gitLabApiWrapper.canSignIn(sampleJwt);
        }
    }

    private boolean tryToSignInWhenNoExceptionsAreThrown() throws GitLabApiException {
        try (MockedConstruction<GitLabApi> ignore = mockConstruction(GitLabApi.class,
                (mock, context) -> when(mock.getUserApi()).thenReturn(userApi))) {
            when(userApi.getCurrentUser()).thenReturn(sampleUser);
            return gitLabApiWrapper.canSignIn(sampleJwt);
        }
    }
}