package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static JwtService jwtService;
    private final String patJwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkdW1teVVzZXJuYW1lIiwidHlwZSI6IlBBVCIsImlhdCI6MTYxNDQ5NzM0NH0.-OcEu0Kzl9Eclgi_AZM8c-LoHlukAil3zkkOoCM514o";
    private final String userPassJwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkdW1teVVzZXJuYW1lIiwidHlwZSI6IlVTRVJfUEFTUyIsImlhdCI6MTYxNDQ5NzI1M30.z1921WRkAdWRsZ4WJt2Td6QHO9yU7xK-vpT-Oxf2rag";
    private final String badSignatureJwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkdW1teVVzZXJuYW1lIiwidHlwZSI6IlBBVCIsImlhdCI6MTYxNDQ5ODE1OH0.vNfAdIJYySYZZuXZYLNrddYpNyjZPnERcwcaHqdNE2I";

    @BeforeAll
    static void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void createJwt() {
    }

    @Test
    void jwtSignatureOkPat() {
        assertTrue(jwtService.jwtIsValid(patJwt));
    }

    @Test
    void jwtSignatureOkUserPass() {
        assertTrue(jwtService.jwtIsValid(userPassJwt));
    }

    @Test
    void jwtSignatureFailForBadSignature() {
        assertFalse(jwtService.jwtIsValid(badSignatureJwt));
    }

    @Test
    void jwtSignatureFailForMalformedJwt() {
        assertFalse(jwtService.jwtIsValid("bad jwt"));
    }

    @Test
    void getSuccessfulTypeForPatJwt() {
        assertEquals(JwtService.JwtType.PAT, jwtService.getType(patJwt));
    }

    @Test
    void getSuccessfulTypeForUserPassJwt() {
        assertEquals(JwtService.JwtType.USER_PASS, jwtService.getType(userPassJwt));
    }

    @Test
    void throwSignatureExceptionWhenGettingJwtType() {
        assertNull(jwtService.getType(badSignatureJwt));
    }
}