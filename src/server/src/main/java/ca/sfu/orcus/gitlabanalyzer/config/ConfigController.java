package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    @PostMapping(value = "/api/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addConfig(@CookieValue(value = "sessionId") String jwt,
                          @RequestBody ConfigDto configDto,
                          HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            tryAddingNewConfigByJwt(jwt, configDto, response);
        } else {
            response.setStatus(401);
        }
    }

    @DeleteMapping("/api/config/{configId}")
    public void deleteConfig(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable("configId") String configId,
                             HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            configService.removeConfigById(configId);
            response.setStatus(200);
        } else {
            response.setStatus(401);
        }
    }

    @GetMapping("/api/config/{configId}")
    public String getConfigById(@CookieValue(value = "sessionId") String jwt,
                                @PathVariable("configId") String configId,
                                HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            response.setStatus(200);
            return configService.getConfigJsonById(configId);
        } else {
            response.setStatus(401);
            return "";
        }
    }

    @GetMapping("/api/configs")
    public String getAllConfigsByJwt(@CookieValue(value = "sessionId") String jwt,
                                     HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            response.setStatus(200);
            return configService.getAllConfigJsonsByJwt(jwt);
        } else {
            response.setStatus(401);
            return "";
        }
    }

    private void tryAddingNewConfigByJwt(String jwt, ConfigDto configDto, HttpServletResponse response) {
        try {
            String configId = configService.addNewConfigByJwt(jwt, configDto);
            response.setStatus(200);
            addConfigIdToResponse(response, configId);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    private void addConfigIdToResponse(HttpServletResponse response, String configId) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        out.print(gson.toJson(new ConfigIdDto(configId)));
        out.flush();
    }

    private static final class ConfigIdDto {
        private String id;

        public ConfigIdDto(String id) {
            this.id = id;
        }
    }
}
