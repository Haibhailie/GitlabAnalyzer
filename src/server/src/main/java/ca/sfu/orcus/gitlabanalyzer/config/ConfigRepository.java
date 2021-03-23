package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        this.configsCollection = database.getCollection("CONFIGS_COLLECTION");
        this.userConfigsCollection = database.getCollection("USER_CONFIGS_COLLECTION");
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
        return configDoc != null ? configDoc.getInteger("numSubscribers") : 0;
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
        Document configDoc = collection.find(eq("_id", configId)).first();

        if (configDoc == null) {
            return Optional.empty();
        }

        String configJson = getConfigJsonFromConfigDocument(configDoc);
        return Optional.of(configJson);
    }

    public List<ConfigDto> getAllConfigDtosByJwt(String jwt) {
        List<ConfigDto> configDtos = new ArrayList<>();
        FindIterable<Document> configDocs = collection.find(eq("jwt", jwt));

        for (Document configDoc : configDocs) {
            configDtos.add(getConfigDtoFromConfigDocument(configDoc));
        }

        return configDtos;
    }

    private boolean containsUser(int userId) {
        Document userConfigsDocument = userConfigsCollection.find(eq("userId", userId)).first();
        return (userConfigsDocument != null);
    }

    private String getConfigJsonFromConfigDocument(Document configDoc) {
        return configDoc.getString("config");
    }

    private ConfigDto getConfigDtoFromConfigDocument(Document configDoc) {
        String configJson = getConfigJsonFromConfigDocument(configDoc);
        return gson.fromJson(configJson, ConfigDto.class);
    }
}
