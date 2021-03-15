package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class ConfigRepository {
    MongoCollection<Document> collection;
    private static final Gson gson = new Gson();

    public ConfigRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        this.collection = database.getCollection("CONFIGS_COLLECTION");
    }

    public String addNewConfigByJwt(String jwt, ConfigDto configDto) {
        String configId = new ObjectId().toString();
        configDto.setId(configId);
        collection.insertOne(generateNewConfigDoc(jwt, configId, configDto));
        return configId;
    }

    public void removeConfigById(String configId) {
        collection.deleteOne(eq("_id", configId));
    }

    private Document generateNewConfigDoc(String jwt, String configId, ConfigDto configDto) {
        String configJson = gson.toJson(configDto);

        return new Document("_id", configId)
                .append("jwt", jwt)
                .append("config", configJson);
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

    private String getConfigJsonFromConfigDocument(Document configDoc) {
        return configDoc.getString("config");
    }

    private ConfigDto getConfigDtoFromConfigDocument(Document configDoc) {
        String configJson = getConfigJsonFromConfigDocument(configDoc);
        return gson.fromJson(configJson, ConfigDto.class);
    }
}
