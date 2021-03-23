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
        this.configsCollection = database.getCollection("TEST_CONFIGS_COLLECTION");
        this.userConfigsCollection = database.getCollection("TEST_USER_CONFIGS_COLLECTION");
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
            userConfigsCollection.updateOne(eq("_userId", userId), addToSet("configIds", configId));
        }
    }

    public void deleteConfigForUser(int userId, String configId) {
        userConfigsCollection.updateOne(eq("_userId", userId), pull("configIds", configId));
        configsCollection.updateOne(eq("_id", configId), inc("numSubscribers", -1));
    }

    public void deleteConfig(String configId) {
        configsCollection.deleteOne(eq("_id", configId));
    }

    public int getNumSubscribersOfConfig(String configId) {
        Document configDoc = configsCollection.find(eq("_id", configId)).first();
        return (configDoc == null) ? 0 : configDoc.getInteger("numSubscribers");
    }

    private Document generateNewConfigDoc(String configId, ConfigDto configDto, int numSubscribers) {
        String configJson = gson.toJson(configDto);

        return new Document("_id", configId)
                .append("config", configJson)
                .append("numSubscribers", numSubscribers);
    }

    private Document generateNewUserConfigsDoc(int userId, List<String> configIds) {
        return new Document("_userId", userId)
                .append("configIds", configIds);
    }

    public Optional<String> getConfigJsonById(String configId) {
        Document configDoc = configsCollection.find(eq("_id", configId)).first();

        return (configDoc == null) ? Optional.empty() :
                Optional.of(getConfigJsonFromConfigDocument(configDoc));
    }

    public Optional<ConfigDto> getConfigDtoById(String configId) {
        Document configDoc = configsCollection.find(eq("_id", configId)).first();

        return (configDoc == null) ? Optional.empty() :
                Optional.of(getConfigDtoFromConfigDocument(configDoc));
    }

    private boolean containsUser(int userId) {
        Document userConfigsDoc = userConfigsCollection.find(eq("_userId", userId)).first();
        return (userConfigsDoc != null);
    }

    public boolean userHasConfig(int userId, String configId) {
        Document userConfigsDoc = userConfigsCollection.find(and(
                eq("_userId", userId),
                eq("configIds", configId))).first();
        return (userConfigsDoc != null);
    }

    public Optional<List<String>> getAllConfigIdsForCurrentUser(int userId) {
        Document userConfigsDoc = userConfigsCollection.find(eq("_userId", userId)).first();
        return (userConfigsDoc == null) ? Optional.empty() :
                Optional.of(userConfigsDoc.getList("configIds", String.class));
    }

    private ConfigDto getConfigDtoFromConfigDocument(Document configDoc) {
        String configJson = getConfigJsonFromConfigDocument(configDoc);
        return gson.fromJson(configJson, ConfigDto.class);
    }

    private String getConfigJsonFromConfigDocument(Document configDoc) {
        return configDoc.getString("config");
    }
}
