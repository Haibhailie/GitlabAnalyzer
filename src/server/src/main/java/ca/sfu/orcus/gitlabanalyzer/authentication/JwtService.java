package ca.sfu.orcus.gitlabanalyzer.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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
    Key secretKey;

    @Autowired
    public JwtService() {
        String encodedKey = System.getenv("SECRET");
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedKey));
    }

    public String createJwt(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromJwt(String jwt) {
        try {
            Jws<Claims> claimsJws;
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
            return claimsJws.getBody().getSubject();
        } catch (SignatureException e) {
            return null;
        }
    }
}
