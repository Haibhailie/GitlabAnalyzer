package ca.sfu.orcus.gitlabanalyzer.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    enum JwtType {PAT, USER_PASS}

    Key secretKey;

    @Autowired
    public JwtService() {
        String encodedKey = System.getenv("SECRET");
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedKey));
    }

    public String createJwt(AuthenticationUser user, JwtType type) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", type)
                .setIssuedAt(new Date())
                .signWith(secretKey)
                .compact();
    }

    public boolean jwtSignatureOk(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }

    public JwtType getType(String jwt) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
            String typeString = claims.getBody().get("type", String.class);
            return JwtType.valueOf(typeString);

        } catch (SignatureException e) {
            return null;
        }
    }
}
