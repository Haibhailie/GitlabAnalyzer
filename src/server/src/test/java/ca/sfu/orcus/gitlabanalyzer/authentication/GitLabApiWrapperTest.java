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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitLabApiWrapperTest {
    @Mock JwtService jwtService;
    @Mock AuthenticationRepository authRepository;
    @Mock UserApi userApi;

    @InjectMocks
    GitLabApiWrapper gitLabApiWrapper;

    private final String sampleJwt = "sampleJwt";
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
    public void failGitLabSignInFromBadPatJwt() throws GitLabApiException {
        setupForSignInWithPatJwt();
        assertFalse(tryToSignInWhenGitLabApiThrowsException());
    }

    @Test
    public void failGitLabSignInFromBadUserPassJwt() throws GitLabApiException {
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