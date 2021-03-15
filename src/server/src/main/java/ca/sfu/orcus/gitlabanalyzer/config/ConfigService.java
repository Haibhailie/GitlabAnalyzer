package ca.sfu.orcus.gitlabanalyzer.config;

import com.google.gson.Gson;
import org.bson.Document;
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

    String addConfig(String jwt, ConfigDto configDto) {
        return configRepository.addNewConfigByJwt(jwt, configDto);
    }

    String getConfigJsonById(String id) {
        Optional<String> configJson = configRepository.getConfigJsonById(id);
        return configJson.orElse("");
    }

    String getConfigJsonsByJwt(String jwt) {
        List<String> configJsons = configRepository.getConfigJsonsByJwt(jwt);
        Gson gson = new Gson();
        return gson.toJson(configJsons);
    }

}
