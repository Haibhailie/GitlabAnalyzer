package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public ConfigService(ConfigRepository configRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.configRepository = configRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    String addNewConfig(String jwt, ConfigDto configDto) throws GitLabApiException {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        int userId = gitLabApi.getUserApi().getCurrentUser().getId();

        String generatedConfigId = configRepository.addNewConfig(configDto);
        configRepository.addConfigToUserProfile(userId, generatedConfigId);
        return generatedConfigId;
    }

    void deleteConfig(String jwt, String configId) throws GitLabApiException {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        int userId = gitLabApi.getUserApi().getCurrentUser().getId();

        configRepository.deleteConfigForUser(userId, configId);

        if (configRepository.getNumSubscribersOfConfig(configId) == 0) {
            configRepository.deleteConfig(configId);
        }
    }

    String getConfigJsonById(String configId) {
        Optional<String> configJson = configRepository.getConfigJsonById(configId);
        return configJson.orElse("");
    }

    String getAllConfigJsonsByJwt(String jwt) {
        List<ConfigDto> configDtos = configRepository.getAllConfigDtosByJwt(jwt);
        Gson gson = new Gson();
        return gson.toJson(configDtos);
    }

}
