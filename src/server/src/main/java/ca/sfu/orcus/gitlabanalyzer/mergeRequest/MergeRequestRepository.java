package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MergeRequestRepository {

    private final MongoCollection<Document> mergeRequestCollection;

    private enum MergeRequest {
        documentId("mergeRequestId"),
        projectUrl("projectUrl"),
        title("title"),
        author("author"),
        authorId("authorId"),
        description("description"),
        time("time"),
        webUrl("webUrl"),
        sumOfCommitsScore("sumOfCommitsScore"),
        committerNames("committerNames"),
        commits("commits"),
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

    public List<String> cacheAllMergeRequests(List<MergeRequestDtoDb> mergeRequestDtoDbs, String projectUrl) {
        List<String> documentIds = new ArrayList<>();
        for (MergeRequestDtoDb presentMergeRequest : mergeRequestDtoDbs) {
            if (!mergeRequestAlreadyCached(presentMergeRequest, projectUrl)) {
                String documentId = cacheMergeRequest(presentMergeRequest, projectUrl);
                documentIds.add(documentId);
            }
        }
        return documentIds;
    }

    /*private boolean mergeRequestAlreadyCached(MergeRequestDtoDb mergeRequest, String projectUrl) {
        Document mergeRequestDoc = mergeRequestCollection.find()
    }*/

    private String cacheMergeRequest(MergeRequestDtoDb mergeRequest, String projectUrl) {
        String documentId = new ObjectId().toString();
        Document mergeRequestDocument = generateMergeRequestDocument(mergeRequest, documentId, projectUrl);
        mergeRequestCollection.insertOne(mergeRequestDocument);
        return documentId;
    }

    private Document generateMergeRequestDocument(MergeRequestDtoDb mergeRequest, String documentId, String projectUrl) {
        return new Document(MergeRequest.documentId.key, documentId)
                .append(MergeRequest.projectUrl.key, projectUrl)
                .append(MergeRequest.title.key, mergeRequest.getTitle())
                .append(MergeRequest.author.key, mergeRequest.getAuthor())
                .append(MergeRequest.authorId.key, mergeRequest.getAuthorId())
                .append(MergeRequest.description.key, mergeRequest.getDescription())
                .append(MergeRequest.webUrl.key, mergeRequest.getWebUrl())
                .append(MergeRequest.sumOfCommitsScore.key, mergeRequest.getSumOfCommitsScore())
                .append(MergeRequest.committerNames.key, mergeRequest.getCommitterNames())
                .append(generateCommitDocuments(mergeRequest))
                .append(generateFileDocuments(mergeRequest))
                .append(MergeRequest.isIgnored.key, mergeRequest.isIgnored());
    }


}

