package ca.sfu.orcus.gitlabanalyzer.authentication;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.*;

@Repository
public class AuthenticationRepository {
    MongoCollection<Document> collection;

    public AuthenticationRepository() {
        MongoClient mongoClient = MongoClients.create(System.getenv("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(System.getenv("DATABASE"));
        this.collection = database.getCollection(System.getenv("USERS-COLLECTION"));
    }

    public void addNewUser(User newUser) {
        collection.insertOne(generateUserPatDoc(newUser));
    }

    private Document generateUserPatDoc(User newUser) {
        return new Document("_id", new ObjectId())
                .append("type", "test_pat")
                .append("pat", newUser.getPat())
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

}
