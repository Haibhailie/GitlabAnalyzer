package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class AuthenticationRepository {
    MongoCollection<Document> collection;

    public AuthenticationRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        this.collection = database.getCollection(VariableDecoderUtil.decode("USERS_COLLECTION"));
    }

    public void addNewUserByPat(AuthenticationUser newUser) {
        collection.insertOne(generateUserPatDoc(newUser));
    }

    public void addNewUserByUserPass(AuthenticationUser newUser) {
        collection.insertOne(generateUserAuthTokenDoc(newUser));
    }

    private Document generateUserPatDoc(AuthenticationUser newUser) {
        return new Document("_id", new ObjectId())
                .append("type", "pat")
                .append("pat", newUser.getPat())
                .append("jwt", newUser.getJwt());
    }

    private Document generateUserAuthTokenDoc(AuthenticationUser newUser) {
        return new Document("_id", new ObjectId())
                .append("type", "authToken")
                .append("authToken", newUser.getAuthToken())
                .append("jwt", newUser.getJwt());
    }

    public boolean contains(String jwt) {
        Document user = collection.find(eq("jwt", jwt)).first();
        return (user != null);
    }

    public String getPatFor(String jwt) {
        Document user = collection.find(eq("jwt", jwt)).first();
        return user.getString("pat");
    }

    public String getAuthTokenFor(String jwt) {
        Document user = collection.find(eq("jwt", jwt)).first();
        return user.getString("authToken");
    }
}
