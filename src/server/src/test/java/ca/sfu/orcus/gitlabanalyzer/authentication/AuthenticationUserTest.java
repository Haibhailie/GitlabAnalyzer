package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationUserTest {
    private static final String pat = System.getenv("SAMPLE_PAT");
    private static final String username = System.getenv("SAMPLE_USERNAME");
    private static final String password = System.getenv("SAMPLE_PASSWORD");
    private static final String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkdW1teVVzZXJuYW1lIiwidHlwZSI6IlBBVCIsImlhdCI6MTYxNDQ5NzM0NH0.-OcEu0Kzl9Eclgi_AZM8c-LoHlukAil3zkkOoCM514o";

    @Test
    public void createAuthenticationUserFromPat() {
        AuthenticationUser user = new AuthenticationUser(pat);
        assertEquals(pat, user.getPat());
    }

    @Test
    public void createAuthenticationUserFromUserPass() {
        AuthenticationUser user = new AuthenticationUser(username, password);
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
    }

    @Test
    public void setAndGetJwt() {

    }



}