package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthenticationController {
    private final AuthenticationService authService;

    @Autowired
    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public ModelAndView loadIndex() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @PostMapping("/api/auth")
    public void loginWithPat(@RequestBody AuthenticationUser user,
                             HttpServletResponse response) {
        try {
            String jwt = authService.signInWithPat(user);
            Cookie cookie = createSessionIdCookie(jwt);
            response.addCookie(cookie);
            response.setStatus(SC_OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            response.setStatus(SC_BAD_REQUEST);
        }
    }

    @PostMapping("/api/signin")
    public void loginWithUserPass(@RequestBody AuthenticationUser user,
                                  HttpServletResponse response) {
        try {
            String jwt = authService.signInWithUserPass(user);
            Cookie cookie = createSessionIdCookie(jwt);
            response.addCookie(cookie);
            response.setStatus(SC_OK);
        } catch (IllegalArgumentException e) {
            response.setStatus(SC_UNAUTHORIZED);
        } catch (BadRequestException e) {
            response.setStatus(SC_BAD_REQUEST);
        }
    }

    @PostMapping("/api/signout")
    public void logoutWithCookie(@CookieValue(value = "sessionId") String jwt,
                                  HttpServletResponse response) {
        Cookie cookie = new Cookie("sessionId", jwt);
        cookie.setMaxAge(0); // Set cookie age as 0 to delete an existing cookie.
        response.setStatus(SC_OK);
    }

    @GetMapping("/api/ping")
    public void checkJwtIsValid(@CookieValue(value = "sessionId") String jwt,
                                HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            response.setStatus(SC_OK);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    private Cookie createSessionIdCookie(String jwt) {
        Cookie cookie = new Cookie("sessionId", jwt);
        cookie.setMaxAge(60 * 60 * 24 * 30); // sets cookie expiry to 1 month
        return cookie;
    }
}
