package ca.sfu.orcus.gitlabanalyzer.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtTokenCreator {
    public static String createJwt(User user) {
        String encodedKey = System.getenv("SECRET");
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedKey));

        return Jwts.builder()
                .setSubject(user.username)
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }
}
