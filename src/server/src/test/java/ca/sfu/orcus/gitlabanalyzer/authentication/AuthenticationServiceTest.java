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

    @Test
    public void failRegisterNewUserByPatForNullPat() {
        AuthenticationUser user = new AuthenticationUser(null);
        assertThrows(BadRequestException.class, () -> authService.registerNewPat(user));
    }

    @Test
    public void failRegisterNewUserByPatUnknownToGitLab() throws GitLabApiException {
        AuthenticationUser user = new AuthenticationUser("unknownPat");
        when(gitLabApiWrapper.getUsernameFromPat(user.getPat())).thenThrow(GitLabApiException.class);
        assertThrows(IllegalArgumentException.class, () -> authService.registerNewPat(user));
    }

    @Test
    public void successfullyRegisterUserByPat() {
        AuthenticationUser user = new AuthenticationUser("examplePat");
        when(jwtService.createJwt(user, JwtService.JwtType.PAT)).thenReturn(sampleJwt);
        assertNotNull(authService.registerNewPat(user));
    }

    @Test
    public void failRegisterNewUserByUserPassForNullUsername() {
        AuthenticationUser user = new AuthenticationUser(null, "mySecurePassword");
        assertThrows(BadRequestException.class, () -> authService.registerNewUserPass(user));
    }

    @Test
    public void failRegisterNewUserByUserPassForNullPassword() {
        AuthenticationUser user = new AuthenticationUser("user", null);
        assertThrows(BadRequestException.class, () -> authService.registerNewUserPass(user));
    }

    @Test
    public void failRegisterNewUserByUserPassUnknownToGitLab() {
        AuthenticationUser user = new AuthenticationUser("unknownUser", "password");
        when(gitLabApiWrapper.getOAuth2AuthToken(user.getUsername(), user.getPassword())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.registerNewUserPass(user));
    }

    @Test
    public void successfullyRegisterUserByUsernameAndPassword() {
        AuthenticationUser user = new AuthenticationUser("user", "securePassword");
        when(gitLabApiWrapper.getOAuth2AuthToken(user.getUsername(), user.getPassword())).thenReturn(Optional.of("authToken"));
        when(jwtService.createJwt(user, JwtService.JwtType.USER_PASS)).thenReturn(sampleJwt);
        assertNotNull(authService.registerNewUserPass(user));
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