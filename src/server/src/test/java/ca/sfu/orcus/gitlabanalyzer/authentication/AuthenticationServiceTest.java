package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock private static JwtService jwtService;
    @Mock private static AuthenticationRepository authRepository;
    @Mock private static GitLabApiWrapper gitLabApiWrapper;

    @InjectMocks
    private AuthenticationService authService;

    String sampleJwt = "sampleJwt";
    String samplePat = "samplePat";

    @Test
    public void failRegisterNewUserByPatForNullPat() {
        AuthenticationUser user = new AuthenticationUser(null);
        assertThrows(BadRequestException.class, () -> authService.signInWithPat(user));
    }

    @Test
    public void failRegisterNewUserByPatUnknownToGitLab() throws GitLabApiException {
        AuthenticationUser user = new AuthenticationUser("unknownPat");
        when(gitLabApiWrapper.getUsernameFromPat(user.getPat())).thenThrow(GitLabApiException.class);
        assertThrows(IllegalArgumentException.class, () -> authService.signInWithPat(user));
    }

    @Test
    public void successfullyRegisterUserByPat() {
        AuthenticationUser user = new AuthenticationUser(samplePat);
        when(authRepository.containsPat(anyString())).thenReturn(false);
        when(jwtService.createJwt(user, JwtService.JwtType.PAT)).thenReturn(sampleJwt);
        assertNotNull(authService.signInWithPat(user));
    }

    @Test
    public void successfullyGetJwtForAlreadyKnownPat() {
        AuthenticationUser user = new AuthenticationUser(samplePat);
        when(authRepository.containsPat(anyString())).thenReturn(true);
        when(authRepository.getJwtForPat(samplePat)).thenReturn(sampleJwt);
        assertEquals(sampleJwt, authService.signInWithPat(user));
    }

    @Test
    public void failSignInByUserPassForNullUsername() {
        AuthenticationUser user = new AuthenticationUser(null, "mySecurePassword");
        assertThrows(BadRequestException.class, () -> authService.signInWithUserPass(user));
    }

    @Test
    public void failSignInByUserPassForNullPassword() {
        AuthenticationUser user = new AuthenticationUser("user", null);
        assertThrows(BadRequestException.class, () -> authService.signInWithUserPass(user));
    }

    @Test
    public void failRegisterNewUserByUserPassUnknownToGitLab() {
        AuthenticationUser user = new AuthenticationUser("unknownUser", "password");
        when(gitLabApiWrapper.getOAuth2AuthToken(user.getUsername(), user.getPassword())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.signInWithUserPass(user));
    }

    @Test
    public void successfullyRegisterUserByUsernameAndPassword() {
        AuthenticationUser user = new AuthenticationUser("user", "securePassword");
        when(authRepository.containsAuthToken(anyString())).thenReturn(false);
        when(gitLabApiWrapper.getOAuth2AuthToken(user.getUsername(), user.getPassword())).thenReturn(Optional.of("authToken"));
        when(jwtService.createJwt(user, JwtService.JwtType.USER_PASS)).thenReturn(sampleJwt);
        assertNotNull(authService.signInWithUserPass(user));
    }

    @Test
    public void successfullyGetJwtForAlreadyKnownUsernameAndPassword() {
        String sampleAuthToken = "sampleAuthToken";
        AuthenticationUser user = new AuthenticationUser("user", "securePassword");
        when(gitLabApiWrapper.getOAuth2AuthToken("user", "securePassword")).thenReturn(Optional.of(sampleAuthToken));
        when(authRepository.containsAuthToken(sampleAuthToken)).thenReturn(true);
        when(authRepository.getJwtForAuthToken(sampleAuthToken)).thenReturn(sampleJwt);
        assertEquals(sampleJwt, authService.signInWithUserPass(user));
    }

    @Test
    public void jwtInvalidBadJwt() {
        setupJwtConditions(false, true, true);
        assertFalse(authService.jwtIsValid(sampleJwt));
    }

    @Test
    public void jwtInvalidJwtNotInDatabase() {
        setupJwtConditions(true, false, true);
        assertFalse(authService.jwtIsValid(sampleJwt));
    }

    @Test
    public void jwtInvalidGitLabReject() {
        setupJwtConditions(true, true, false);
        assertFalse(authService.jwtIsValid(sampleJwt));
    }

    @Test
    public void jwtIsValid() {
        setupJwtConditions(true, true, true);
        assertTrue(authService.jwtIsValid(sampleJwt));
    }

    private void setupJwtConditions(boolean jwtIsValid,
                                    boolean jwtIsInDatabase,
                                    boolean canSignIntoGitlab) {
        lenient().when(jwtService.jwtIsValid(sampleJwt)).thenReturn(jwtIsValid);
        lenient().when(authRepository.contains(sampleJwt)).thenReturn(jwtIsInDatabase);
        lenient().when(gitLabApiWrapper.canSignIn(sampleJwt)).thenReturn(canSignIntoGitlab);
    }

}