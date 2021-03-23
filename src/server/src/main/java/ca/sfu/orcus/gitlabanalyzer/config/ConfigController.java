package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.servlet.http.HttpServletResponse.*;

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

    @PostMapping(value = "/api/config",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void addConfig(@CookieValue(value = "sessionId") String jwt,
                          @RequestBody ConfigDto configDto,
                          HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            tryAddingNewConfig(jwt, configDto, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    @DeleteMapping("/api/config/{configId}")
    public void deleteConfig(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable("configId") String configId,
                             HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            tryDeletingConfig(jwt, configId, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
        }
    }

    @GetMapping("/api/config/{configId}")
    public String getConfigForCurrentUser(@CookieValue(value = "sessionId") String jwt,
                                          @PathVariable("configId") String configId,
                                          HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            return tryGettingConfigForCurrentUser(jwt, configId, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
            return "";
        }
    }

    @GetMapping("/api/configs")
    public String getAllConfigsForCurrentUser(@CookieValue(value = "sessionId") String jwt,
                                              HttpServletResponse response) {
        if (authService.jwtIsValid(jwt)) {
            return tryGettingAllConfigsForCurrentUser(jwt, response);
        } else {
            response.setStatus(SC_UNAUTHORIZED);
            return "";
        }
    }

    private void tryAddingNewConfig(String jwt, ConfigDto configDto, HttpServletResponse response) {
        try {
            String configId = configService.addNewConfig(jwt, configDto);
            response.setStatus(SC_OK);
            addConfigIdToResponse(response, configId);
        } catch (IOException | GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
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

    private void tryDeletingConfig(String jwt, String configId, HttpServletResponse response) {
        try {
            configService.deleteConfig(jwt, configId);
            response.setStatus(SC_OK);
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String tryGettingConfigForCurrentUser(String jwt, String configId, HttpServletResponse response) {
        try {
            String configJson = configService.getConfigJsonForCurrentUser(jwt, configId);
            response.setStatus(configJson.isEmpty() ? SC_NOT_FOUND : SC_OK);
            return configJson;
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    private String tryGettingAllConfigsForCurrentUser(String jwt, HttpServletResponse response) {
        try {
            response.setStatus(SC_OK);
            return configService.getAllConfigJsonsForCurrentUser(jwt);
        } catch (GitLabApiException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // POST response body object
    private static final class ConfigIdDto {
        private String id;

        public ConfigIdDto(String id) {
            this.id = id;
        }
    }
}
