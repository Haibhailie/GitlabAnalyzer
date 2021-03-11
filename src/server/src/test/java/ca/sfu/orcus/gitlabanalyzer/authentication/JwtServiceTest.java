package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;

import javax.crypto.SecretKey;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtServiceTest {
    private JwtService jwtService;
    private SecretKey secretKey;

    @BeforeAll
    public void setup() {
        String encodedSecretKey = generateBase64EncodedSecretKey();
        jwtService = createJwtServiceWithSecretKey(encodedSecretKey);
        secretKey = getSecretKeyFromBase64EncodedString(encodedSecretKey);
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


}