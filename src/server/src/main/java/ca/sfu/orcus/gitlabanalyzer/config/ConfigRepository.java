package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

@Repository
public class ConfigRepository {
    MongoCollection<Document> configsCollection;
    MongoCollection<Document> userConfigsCollection;
    private static final Gson gson = new Gson();

    public ConfigRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        this.configsCollection = database.getCollection(VariableDecoderUtil.decode("CONFIGS_COLLECTION"));
        this.userConfigsCollection = database.getCollection(VariableDecoderUtil.decode("USER_CONFIGS_COLLECTION"));
    }

    // BSON document keys for documents to be stored in Configs collection
    private enum Config {
        id("_id"),
        config("config"),
        numSubscribers("numSubscribers");

        public final String key;

        Config(String key) {
            this.key = key;
        }
    }

    // BSON document keys for documents to be stored in UserConfigs collection
    private enum UserConfig {
        userId("_userId"),
        configIds("configIds"),
        currentConfig("currentConfig");

        public final String key;

        UserConfig(String key) {
            this.key = key;
        }
    }

    public String addNewConfig(ConfigDto configDto) {
        String configId = new ObjectId().toString();
        configDto.setId(configId);
        configsCollection.insertOne(generateNewConfigDoc(configId, configDto, 1));
        return configId;
    }

    public void addConfigToUserProfile(int userId, String configId) {
        if (!containsUser(userId)) {
            userConfigsCollection.insertOne(generateNewUserConfigsDoc(userId, Collections.singletonList(configId)));
        } else {
            userConfigsCollection.updateOne(
                    eq(UserConfig.userId.key, userId),
                    addToSet(UserConfig.configIds.key, configId));
        }

        configsCollection.updateOne(eq(Config.id.key, configId), inc(Config.numSubscribers.key, 1));
    }

    public void updateConfig(ConfigDto configDto) {
        String configId = configDto.getId();
        int numSubscribers = getNumSubscribersOfConfig(configId);
        configsCollection.replaceOne(
                eq(Config.id.key, configId),
                generateNewConfigDoc(configId, configDto, numSubscribers));
    }

    public void updateCurrentConfigForUser(int userId, ConfigDto currentConfigDto) {
        String currentConfigJson = gson.toJson(currentConfigDto);

        if (!containsUser(userId)) {
            userConfigsCollection.insertOne(generateNewUserConfigsDoc(userId, currentConfigJson));
        } else {
            userConfigsCollection.updateOne(
                    eq(UserConfig.userId.key, userId),
                    set(UserConfig.currentConfig.key, currentConfigJson));
        }
    }

    public void deleteConfigForUser(int userId, String configId) {
        userConfigsCollection.updateOne(eq(UserConfig.userId.key, userId), pull(UserConfig.configIds.key, configId));
        configsCollection.updateOne(eq(Config.id.key, configId), inc(Config.numSubscribers.key, -1));

        deleteUserConfigIfNoConfigIds(userId);
        deleteConfigIfNoSubscribers(configId);
    }

    private void deleteUserConfigIfNoConfigIds(int userId) {
        if (getNumConfigIdsForUser(userId) == 0) {
            deleteUserConfig(userId);
        }
    }

    private void deleteConfigIfNoSubscribers(String configId) {
        if (getNumSubscribersOfConfig(configId) == 0) {
            deleteConfig(configId);
        }
    }

    private void deleteUserConfig(int userId) {
        userConfigsCollection.deleteOne(eq(UserConfig.userId.key, userId));
    }

    private void deleteConfig(String configId) {
        configsCollection.deleteOne(eq(Config.id.key, configId));
    }

    private int getNumConfigIdsForUser(int userId) {
        List<String> configIds = getAllConfigIdsForUser(userId).orElse(new ArrayList<>());
        return configIds.size();
    }

    private int getNumSubscribersOfConfig(String configId) {
        Document configDoc = configsCollection.find(eq(Config.id.key, configId)).first();
        return (configDoc == null) ? 0 : configDoc.getInteger(Config.numSubscribers.key);
    }

    private Document generateNewConfigDoc(String configId, ConfigDto configDto, int numSubscribers) {
        String configJson = gson.toJson(configDto);

        return new Document(Config.id.key, configId)
                .append(Config.config.key, configJson)
                .append(Config.numSubscribers.key, numSubscribers);
    }

    private Document generateNewUserConfigsDoc(int userId, List<String> configIds) {
        return new Document(UserConfig.userId.key, userId)
                .append(UserConfig.configIds.key, configIds);
    }

    private Document generateNewUserConfigsDoc(int userId, String currentConfig) {
        return new Document(UserConfig.userId.key, userId)
                .append(UserConfig.currentConfig.key, currentConfig);
    }

    public Optional<String> getConfigJsonById(String configId) {
        Document configDoc = configsCollection.find(eq(Config.id.key, configId)).first();

        return (configDoc == null) ? Optional.empty() :
                Optional.of(getConfigJsonFromConfigDocument(configDoc));
    }

    public Optional<ConfigDto> getConfigDtoById(String configId) {
        Document configDoc = configsCollection.find(eq(Config.id.key, configId)).first();

        return (configDoc == null) ? Optional.empty() :
                Optional.of(getConfigDtoFromConfigDocument(configDoc));
    }

    private boolean containsUser(int userId) {
        Document userConfigsDoc = userConfigsCollection.find(eq(UserConfig.userId.key, userId)).first();
        return (userConfigsDoc != null);
    }

    public boolean containsConfig(String configId) {
        Document configDoc = configsCollection.find(eq(Config.id.key, configId)).first();
        return (configDoc != null);
    }

    public boolean userHasConfig(int userId, String configId) {
        Document userConfigsDoc = userConfigsCollection.find(and(
                eq(UserConfig.userId.key, userId),
                eq(UserConfig.configIds.key, configId))).first();
        return (userConfigsDoc != null);
    }

    public Optional<List<String>> getAllConfigIdsForUser(int userId) {
        Document userConfigsDoc = userConfigsCollection.find(eq(UserConfig.userId.key, userId)).first();
        return (userConfigsDoc == null) ? Optional.empty() :
                Optional.of(userConfigsDoc.getList(UserConfig.configIds.key, String.class));
    }

    private ConfigDto getConfigDtoFromConfigDocument(Document configDoc) {
        String configJson = getConfigJsonFromConfigDocument(configDoc);
        return gson.fromJson(configJson, ConfigDto.class);
    }

    private String getConfigJsonFromConfigDocument(Document configDoc) {
        return configDoc.getString(Config.config.key);
    }
}
