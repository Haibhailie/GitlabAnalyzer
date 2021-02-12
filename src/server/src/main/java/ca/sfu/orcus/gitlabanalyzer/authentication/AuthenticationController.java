package ca.sfu.orcus.gitlabanalyzer.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    private final AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/api/auth")
    public String loginWithPat(@RequestBody PatRequestBody pat, HttpServletResponse response) {
        service.addNewUser(User.fromAuthToken(pat.pat));
    }
}
