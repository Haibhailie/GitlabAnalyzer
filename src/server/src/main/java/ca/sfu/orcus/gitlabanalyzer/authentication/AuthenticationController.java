package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

@RestController
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
    public void loginWithPat(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = authService.addNewUser(user);
            Cookie cookie = createSessionIdCookie(jwt);
            response.addCookie(cookie);
            response.setStatus(200);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
        }
    }

    @PostMapping("/api/signin")
    public void loginWithUserPass(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = authService.addNewUserByUserPass(user);
            Cookie cookie = createSessionIdCookie(jwt);
            response.addCookie(cookie);
            response.setStatus(200);
        } catch (IllegalArgumentException e) {
            response.setStatus(401);
        } catch (BadRequestException e) {
            response.setStatus(400);
        }
    }

    private Cookie createSessionIdCookie(String jwt) {
        Cookie cookie = new Cookie("sessionId", jwt);
        cookie.setMaxAge(60 * 60 * 24 * 30); // sets cookie expiry to 1 month
        return cookie;
    }
}
