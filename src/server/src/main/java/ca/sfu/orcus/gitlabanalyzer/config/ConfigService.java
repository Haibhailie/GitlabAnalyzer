package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import com.google.gson.Gson;
import javassist.NotFoundException;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private static final Gson gson = new Gson();

    @Autowired
    public ConfigService(ConfigRepository configRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.configRepository = configRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public String addNewConfig(String jwt, ConfigDto configDto) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        String generatedConfigId = configRepository.addNewConfig(configDto);
        configRepository.addConfigToUserProfile(userId, generatedConfigId);
        return generatedConfigId;
    }

    public void deleteConfigForUser(String jwt, String configId) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        if (configRepository.userHasConfig(userId, configId)) {
            configRepository.deleteConfigForUser(userId, configId);
        }
    }

    public String getConfigJsonForCurrentUser(String jwt, String configId) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        if (configRepository.userHasConfig(userId, configId)) {
            return configRepository.getConfigJsonById(configId).orElse("");
        }

        return "";
    }

    public String getAllConfigJsonsForCurrentUser(String jwt) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        List<ConfigDto> configDtos = new ArrayList<>();
        List<String> configIds = configRepository.getAllConfigIdsForUser(userId).orElse(new ArrayList<>());
        for (String id : configIds) {
            Optional<ConfigDto> configDto = configRepository.getConfigDtoById(id);
            configDto.ifPresent(configDtos::add);
        }

        return gson.toJson(configDtos);
    }

    public void updateConfig(String jwt, ConfigDto configDto) throws GitLabApiException, NotFoundException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        String configId = configDto.getId();

        if (!configRepository.userHasConfig(userId, configId)
            || configRepository.getNumSubscribersOfConfig(configId) == 0) {
            throw new NotFoundException("Config not found");
        }

        configRepository.updateConfig(configDto);
    }

    public String importConfigForUser(String jwt, ConfigIdDto configIdDto, HttpServletResponse response) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        String configId = configIdDto.getId();

        String configJson = "";

        if (!configRepository.containsConfig(configId)) {
            response.setStatus(SC_NOT_FOUND);
            return configJson;
        }

        if (!configRepository.userHasConfig(userId, configId)) {
            configRepository.addConfigToUserProfile(userId, configId);
        }

        configJson = configRepository.getConfigJsonById(configId).orElse("");
        response.setStatus(SC_OK);

        return configJson;
    }

    public void updateCurrentConfig(String jwt, ConfigDto configDto) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        configRepository.updateCurrentConfigForUser(userId, configDto);
    }
}
