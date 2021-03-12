package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.UserApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    public void failGetGitLabApiForInvalidJwt() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(false);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Test
    public void failGetGitLabApiForInvalidPat() throws GitLabApiException {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.PAT);
        String samplePat = "samplePat";
        when(authRepository.getPatFor(sampleJwt)).thenReturn(samplePat);

        assertNull(getGitLabApiWithGetUserApiMocked());
    }

    @Test
    public void failGetGitLabApiForInvalidUserPass() throws GitLabApiException {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(true);
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.USER_PASS);
        String sampleAuthToken = "sampleAuthToken";
        when(authRepository.getAuthTokenFor(sampleJwt)).thenReturn(sampleAuthToken);

        assertNull(getGitLabApiWithGetUserApiMocked());
    }

    private GitLabApi getGitLabApiWithGetUserApiMocked() throws GitLabApiException {
        try (MockedConstruction<GitLabApi> ignore = mockConstruction(GitLabApi.class,
                (mock, context) -> when(mock.getUserApi()).thenReturn(userApi))) {
            when(userApi.getCurrentUser()).thenThrow(GitLabApiException.class);
            return gitLabApiWrapper.getGitLabApiFor(sampleJwt);
        }
    }
}