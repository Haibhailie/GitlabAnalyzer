package ca.sfu.orcus.gitlabanalyzer.authentication;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

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
                .append("pat", newUser.getAuthToken())
                .append("jwt", newUser.getJwt());
    }
}
