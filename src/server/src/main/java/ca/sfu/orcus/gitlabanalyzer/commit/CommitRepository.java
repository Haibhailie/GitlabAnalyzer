package ca.sfu.orcus.gitlabanalyzer.commit;

import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;


@Repository
public class CommitRepository {
    MongoCollection<Document> collection;

    public CommitRepository() {
        MongoClient mongoClient = MongoClients.create(System.getenv("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(System.getenv("DATABASE"));
        this.collection = database.getCollection(System.getenv("USERS-COLLECTION"));
    }

}
