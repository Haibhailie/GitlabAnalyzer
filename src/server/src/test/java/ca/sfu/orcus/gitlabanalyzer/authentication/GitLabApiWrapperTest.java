package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.UserApi;
import org.gitlab4j.api.models.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitLabApiWrapperTest {
    @Mock JwtService jwtService;
    @Mock AuthenticationRepository authRepository;
    @Mock UserApi userApi;
    @Mock GitLabApi gitLabApi;
    @Mock User sampleUser;

    @InjectMocks
    GitLabApiWrapper gitLabApiWrapper;

    private final String sampleJwt = "sampleJwt";
    private final String samplePat = "samplePat";
    private final String sampleUrl = "mygitlab.com";

    @Test
    public void failGetGitLabApiForInvalidJwt() {
        when(jwtService.jwtIsValid(sampleJwt)).thenReturn(false);
        assertNull(gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Disabled
    @Test
    public void failGetGitLabApiForInvalidPat() throws GitLabApiException {
        when(jwtService.getType(sampleJwt)).thenReturn(JwtService.JwtType.PAT);
        when(authRepository.getPatFor(sampleJwt)).thenReturn(samplePat);
        when(gitLabApi.getUserApi()).thenReturn(userApi);
        when(gitLabApi.getUserApi().getCurrentUser()).thenThrow(GitLabApiException.class);
        assertThrows(GitLabApiException.class, () -> gitLabApiWrapper.getGitLabApiFor(sampleJwt));
    }

    @Test
    public void failGettingUsernameForInvalidPat() throws GitLabApiException {
        when(VariableDecoderUtil.decode("GITLAB_URL")).thenReturn(sampleUrl);
        when(new GitLabApi(sampleUrl, samplePat)).thenReturn(gitLabApi);
        when(gitLabApi.getUserApi()).thenReturn(userApi);
        when(userApi.getCurrentUser()).thenReturn(sampleUser);
        when(sampleUser.getUsername()).thenReturn()
    }

}