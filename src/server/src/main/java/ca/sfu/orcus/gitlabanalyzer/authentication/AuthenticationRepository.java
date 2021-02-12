package ca.sfu.orcus.gitlabanalyzer.authentication;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository {
    MongoCollection<Document> collection;

    @Autowired
    public AuthenticationRepository() {
        MongoClient mongoClient = MongoClients.create(System.getenv("MONGO_URI"));
        MongoDatabase TestDB = mongoClient.getDatabase(System.getenv("DATABASE"));
        this.collection = TestDB.getCollection(System.getenv("COLLECTION"));
    }

    public void addNewUser(User newUser) {
        collection.insertOne(generateUserPatDoc(newUser));
    }

    private Document generateUserPatDoc(User newUser) {
        return new Document("_id", new ObjectId())
                .append("pat", newUser.authToken)
                .append("jwt", newUser.jwt);
    }
}
