package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ConfigController {
    private final ConfigService configService;
    private final AuthenticationService authService;

    @Autowired
    public ConfigController(ConfigService configService, AuthenticationService authService) {
        this.configService = configService;
        this.authService = authService;
    }

    @PostMapping("/api/config")
    public void addConfig(@CookieValue(value = "sessionId") String jwt,
                          @RequestBody ConfigDto configDto,
                          HttpServletResponse response) {
    }

    @GetMapping("/api/config/{configId}")
    public String getConfig(@CookieValue(value = "sessionId") String jwt,
                            @PathVariable("configId") int configId,
                            HttpServletResponse response) {
        return "";
    }

    @GetMapping("/api/configs")
    public String getAllConfigs(@CookieValue(value = "sessionId") String jwt,
                                HttpServletResponse response) {
        return "";
    }
}
