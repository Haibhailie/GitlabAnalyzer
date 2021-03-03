package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    private static AuthenticationService authService;

    // Dependencies
    private static JwtService jwtService;
    private static AuthenticationRepository repo;
    private static GitLabApi gitLabApi;

    // Test data
    private static AuthenticationUser user;

    @BeforeAll
    static void setup() {
        repo = mock(AuthenticationRepository.class);
        jwtService = mock(JwtService.class);
        gitLabApi = mock(GitLabApi.class);
        user = new AuthenticationUser("testUser", "testPassword");

        authService = new AuthenticationService(repo, jwtService);
    }

    @Test
    public void jwtIsInvalidForBadJwt() {
        when(jwtService.jwtSignatureOk(anyString())).thenReturn(false);
        when(repo.contains(anyString())).thenReturn(true);

    }

}