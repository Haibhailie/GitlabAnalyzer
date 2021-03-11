package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitLabApiWrapperTest {
    @Mock JwtService jwtService;
    @Mock AuthenticationRepository authRepository;

    @InjectMocks
    GitLabApiWrapper gitLabApiWrapper;

    private final String sampleJwt = "sampleJwt";

    @Test
    public void failGetGitLabApiForInvalidJwt() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(false);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Test
    public void failGetGitLabApiForInvalidPat() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.PAT);
        String samplePat = "samplePat";
        when(authRepository.getPatFor(sampleJwt)).thenReturn(samplePat);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Test
    public void failGetGitLabApiForInvalidUserPass() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.USER_PASS);
        String sampleAuthToken = "sampleAuthToken";
        when(authRepository.getAuthTokenFor(sampleJwt)).thenReturn(sampleAuthToken);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }
}