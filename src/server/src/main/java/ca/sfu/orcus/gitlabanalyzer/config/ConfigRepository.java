package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
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
        this.collection = database.getCollection(VariableDecoderUtil.decode("CONFIGS_COLLECTION"));
    }

    public String addNewConfigByJwt(String jwt, ConfigDto configDto) {
        String configId = new ObjectId().toString();
        collection.insertOne(generateNewConfigDoc(jwt, configId, configDto));
        return configId;
    }

    private Document generateNewConfigDoc(String jwt, String configId, ConfigDto configDto) {
        String configJson = gson.toJson(configDto);

        return new Document("_id", configId)
                .append("jwt", jwt)
                .append("config", configJson);
    }

    private boolean contains(String jwt) {
        Document config = collection.find(eq("jwt", jwt)).first();
        return (config != null);
    }

    public Optional<ConfigDto> getConfigById(String configId) {
        Document configDoc = collection.find(eq("_id", configId)).first();

        if (configDoc == null) {
            return Optional.empty();
        }

        ConfigDto configDto = getConfigDtoFromConfigDocument(configDoc);
        return Optional.of(configDto);
    }

    public List<ConfigDto> getConfigsByJwt(String jwt) {
        List<ConfigDto> configDtos = new ArrayList<>();
        FindIterable<Document> configDocs = collection.find(eq("jwt", jwt));

        for (Document configDoc : configDocs) {
            configDtos.add(getConfigDtoFromConfigDocument(configDoc));
        }

        return configDtos;
    }

    private ConfigDto getConfigDtoFromConfigDocument(Document configDoc) {
        String configJson = configDoc.getString("config");
        return gson.fromJson(configJson, ConfigDto.class);
    }
}
