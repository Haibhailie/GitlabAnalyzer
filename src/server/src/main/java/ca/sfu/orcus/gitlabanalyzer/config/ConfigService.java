package ca.sfu.orcus.gitlabanalyzer.config;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    String addNewConfigByJwt(String jwt, ConfigDto configDto) {
        return configRepository.addNewConfigByJwt(jwt, configDto);
    }

    void removeConfigById(String configId) {
        configRepository.removeConfigById(configId);
    }

    String getConfigJsonById(String configId) {
        Optional<String> configJson = configRepository.getConfigJsonById(configId);
        return configJson.orElse("");
    }

    String getAllConfigJsonsByJwt(String jwt) {
        List<String> configJsons = configRepository.getConfigJsonsByJwt(jwt);
        Gson gson = new Gson();
        return gson.toJson(configJsons);
    }

}
