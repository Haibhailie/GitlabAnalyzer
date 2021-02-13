package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public void loginWithPat(@RequestBody User user, HttpServletResponse response) {
        String jwt = authService.addNewUser(user);
        Cookie cookie = new Cookie("sessionId", jwt);
        cookie.setMaxAge(60 * 60 * 24 * 30); // sets cookie expiry to 1 month
        response.addCookie(cookie);
        response.setStatus(200);
    }
}
