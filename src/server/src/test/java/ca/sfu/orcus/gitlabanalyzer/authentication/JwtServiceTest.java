package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.authentication.JwtService.JwtType;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import javax.crypto.SecretKey;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtServiceTest {
    private JwtService jwtService;
    private SecretKey secretKey;

    private final AuthenticationUser userWithUserPass = new AuthenticationUser("username", "password");
    private final AuthenticationUser userWithPat = new AuthenticationUser("pat");

    @BeforeAll
    public void setup() {
        String encodedSecretKey = generateBase64EncodedSecretKey();
        jwtService = createJwtServiceWithSecretKey(encodedSecretKey);
        secretKey = getSecretKeyFromBase64EncodedString(encodedSecretKey);
    }

    @Test
    public void successfullyCreateJwtWithPat() {
        String jwt = jwtService.createJwt(userWithPat, JwtType.PAT);
        assertTrue(jwtService.jwtIsValid(jwt));
    }

    @Test
    public void successfullyCreateJwtWithUserPass() {
        String jwt = jwtService.createJwt(userWithPat, JwtType.USER_PASS);
        assertTrue(jwtService.jwtIsValid(jwt));
    }

    @Test
    public void jwtIsValidFailsForInvalidSignature() {
        String foreignJwt = getJwtSignedWithForeignSignature();
        assertFalse(jwtService.jwtIsValid(foreignJwt));
    }

    @Test
    public void jwtIsValidFailsForTamperedJwt() {
        String jwt = jwtService.createJwt(userWithPat, JwtType.PAT);
        String tamperedJwt = tamperJwt(jwt);
        assertFalse(jwtService.jwtIsValid(tamperedJwt));
    }

    @Test
    public void getTypeForPatJwt() {
        String patJwt = jwtService.createJwt(userWithPat, JwtType.PAT);
        assertEquals(JwtType.PAT, jwtService.getType(patJwt));
    }

    @Test
    public void getTypeForUserPassJwt() {
        String userPassJwt = jwtService.createJwt(userWithUserPass, JwtType.USER_PASS);
        assertEquals(JwtType.USER_PASS, jwtService.getType(userPassJwt));
    }

    @Test
    public void getTypeForInvalidJwt() {
        String foreignJwt = getJwtSignedWithForeignSignature();
        assertNull(jwtService.getType(foreignJwt));
    }

    private JwtService createJwtServiceWithSecretKey(String encodedSecretKey) {
        try (MockedStatic<VariableDecoderUtil> mockedVariableDecoderUtil = mockStatic(VariableDecoderUtil.class)) {
            mockedVariableDecoderUtil.when(() -> {
                VariableDecoderUtil.decode(anyString());
            }).thenReturn(encodedSecretKey);

            return new JwtService();
        }
    }

    private String generateBase64EncodedSecretKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    private SecretKey getSecretKeyFromBase64EncodedString(String encodedKey) {
        byte[] decodedKey = Decoders.BASE64.decode(encodedKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private String getJwtSignedWithForeignSignature() {
        String foreignEncodedSecretKey;
        SecretKey foreignSecretKey;

        // Make sure the foreign key isn't the same as ours
        do {
            foreignEncodedSecretKey = generateBase64EncodedSecretKey();
            foreignSecretKey = getSecretKeyFromBase64EncodedString(foreignEncodedSecretKey);
        } while (foreignSecretKey.equals(this.secretKey));

        JwtService jwtService = createJwtServiceWithSecretKey(foreignEncodedSecretKey);
        return jwtService.createJwt(userWithPat, JwtType.PAT);
    }

    private String tamperJwt(String jwt) {
        Random rand = new Random();
        int idx = rand.nextInt(jwt.length());
        StringBuilder sb = new StringBuilder(jwt);
        sb.setCharAt(idx, (char) (sb.charAt(idx) - 1));
        return sb.toString();
    }
}