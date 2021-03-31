package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import com.google.gson.Gson;
import javassist.NotFoundException;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        if (!configRepository.containsConfig(configId) || !configRepository.userHasConfig(userId, configId)) {
            throw new NotFoundException("Config not found");
        }

        configRepository.updateConfig(configDto);
    }

    public String importConfigForUser(String jwt, ConfigIdDto configIdDto) throws GitLabApiException, NotFoundException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);
        String configId = configIdDto.getId();

        if (!configRepository.containsConfig(configId)) {
            throw new NotFoundException("Config not found");
        }

        if (!configRepository.userHasConfig(userId, configId)) {
            configRepository.addConfigToUserProfile(userId, configId);
        }

        return configRepository.getConfigJsonById(configId).orElse("");
    }
}
