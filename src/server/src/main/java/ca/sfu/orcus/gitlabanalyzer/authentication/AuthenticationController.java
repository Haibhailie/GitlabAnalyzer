package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth")
    public void loginWithPat(@RequestBody AuthenticationUser user, HttpServletResponse response) {
        try {
            String jwt = authService.addNewUserFromPat(user);
            Cookie cookie = createSessionIdCookie(jwt);
            response.addCookie(cookie);
            response.setStatus(200);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
        }
    }

    @GetMapping("/api/ping")
    public void checkJwtIsValid(@CookieValue(value = "sessionId") String jwt, HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            response.setStatus(200);
        } else {
            response.setStatus(401);
        }
    }

    private Cookie createSessionIdCookie(String jwt) {
        Cookie cookie = new Cookie("sessionId", jwt);
        cookie.setMaxAge(60 * 60 * 24 * 30); // sets cookie expiry to 1 month
        return cookie;
    }
}
