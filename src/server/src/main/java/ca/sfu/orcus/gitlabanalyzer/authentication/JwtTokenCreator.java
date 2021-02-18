package ca.sfu.orcus.gitlabanalyzer.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenCreator {

    @Autowired
    public JwtTokenCreator() {
    }

    public String createJwt(User user) {
        String encodedKey = System.getenv("SECRET");
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedKey));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }
}
