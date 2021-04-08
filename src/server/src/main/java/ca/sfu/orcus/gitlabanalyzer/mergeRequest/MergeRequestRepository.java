package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
public class MergeRequestRepository {

    private final MongoCollection<Document> mergeRequestCollection;
    private enum MergeRequest {
        documentId("mergeRequestId"),
        title("title"),
        author("author"),
        description("description"),
        time("time"),
        webUrl("webUrl"),
        sumOfCommitsScore("sumOfCommitsScore"),
        commits("commits"),
        committers("committers"),
        files("files"),
        isIgnored("isIgnored");

        public String key;

        MergeRequest(String key) {
            this.key = key;
        }
    }

    public MergeRequestRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        mergeRequestCollection = database.getCollection(VariableDecoderUtil.decode("MERGE_REQUEST_COLLECTION"));
    }


}

