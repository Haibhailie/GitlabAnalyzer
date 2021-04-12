package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitRepository;
import ca.sfu.orcus.gitlabanalyzer.file.FileRepository;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.set;

@Repository
public class MergeRequestRepository {
    private final MongoCollection<Document> mergeRequestCollection;
    private final CommitRepository commitRepo;
    private final FileRepository fileRepo;

    public MergeRequestRepository(CommitRepository commitRepo, FileRepository fileRepo) {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        mergeRequestCollection = database.getCollection(VariableDecoderUtil.decode("MERGE_REQUESTS_COLLECTION"));

        this.commitRepo = commitRepo;
        this.fileRepo = fileRepo;
    }

    private enum MergeRequest {
        documentId("_id"),
        mergeRequestId("mergeRequestId"),
        projectUrl("projectUrl"),
        title("title"),
        author("author"),
        userId("userId"),
        description("description"),
        time("time"),
        webUrl("webUrl"),
        commits("commits"),
        committerEmails("committerEmails"),
        sumOfCommitsScore("sumOfCommitsScore"),
        isIgnored("isIgnored"),
        files("files"),
        isSolo("isSolo");

        public String key;

        MergeRequest(String key) {
            this.key = key;
        }
    }

    public List<String> cacheAllMergeRequests(String projectUrl, List<MergeRequestDtoDb> mergeRequestDtoDbs) {
        List<String> documentIds = new ArrayList<>();
        for (MergeRequestDtoDb presentMergeRequest : mergeRequestDtoDbs) {
            String documentId = cacheMergeRequest(presentMergeRequest, projectUrl);
            documentIds.add(documentId);
        }
        return documentIds;
    }

    private String cacheMergeRequest(MergeRequestDtoDb mergeRequest, String projectUrl) {
        Document existingDocument = mergeRequestCollection.find(getMergeRequestEqualityParameter(projectUrl, mergeRequest))
                .projection(include(MergeRequest.documentId.key)).first();
        if (existingDocument != null) {
            String documentId = existingDocument.getString(MergeRequest.documentId.key);
            replaceMergeRequestDocument(documentId, mergeRequest, projectUrl);
            return documentId;
        } else {
            return cacheMergeRequestDocument(mergeRequest, projectUrl);
        }
    }

    private void replaceMergeRequestDocument(String documentId, MergeRequestDtoDb mergeRequest, String projectUrl) {
        Document mergeRequestDocument = generateMergeRequestDocument(mergeRequest, documentId, projectUrl);
        mergeRequestCollection.replaceOne(getMergeRequestEqualityParameter(projectUrl, mergeRequest), mergeRequestDocument);
    }

    private String cacheMergeRequestDocument(MergeRequestDtoDb mergeRequest, String projectUrl) {
        String documentId = new ObjectId().toString();
        Document mergeRequestDocument = generateMergeRequestDocument(mergeRequest, documentId, projectUrl);
        mergeRequestCollection.insertOne(mergeRequestDocument);
        return documentId;
    }

    private Bson getMergeRequestEqualityParameter(String projectUrl, MergeRequestDtoDb mergeRequest) {
        return and(eq(MergeRequest.projectUrl.key, projectUrl), eq(MergeRequest.mergeRequestId.key, mergeRequest.getMergeRequestId()));
    }

    private Document generateMergeRequestDocument(MergeRequestDtoDb mergeRequest, String documentId, String projectUrl) {
        List<Document> commitDocuments = commitRepo.getCommitDocuments(mergeRequest.getCommits());
        List<Document> fileDocuments = fileRepo.getFileDocuments(mergeRequest.getFiles());

        return new Document(MergeRequest.documentId.key, documentId)
                .append(MergeRequest.mergeRequestId.key, mergeRequest.getMergeRequestId())
                .append(MergeRequest.projectUrl.key, projectUrl)
                .append(MergeRequest.title.key, mergeRequest.getTitle())
                .append(MergeRequest.isSolo.key, mergeRequest.isSolo())
                .append(MergeRequest.author.key, mergeRequest.getAuthor())
                .append(MergeRequest.userId.key, mergeRequest.getUserId())
                .append(MergeRequest.description.key, mergeRequest.getDescription())
                .append(MergeRequest.time.key, mergeRequest.getTime())
                .append(MergeRequest.webUrl.key, mergeRequest.getWebUrl())
                .append(MergeRequest.isSolo.key, mergeRequest.getSolo())
                .append(MergeRequest.commits.key, commitDocuments)
                .append(MergeRequest.committerEmails.key, mergeRequest.getCommitterEmails())
                .append(MergeRequest.sumOfCommitsScore.key, mergeRequest.getSumOfCommitsScore())
                .append(MergeRequest.isIgnored.key, mergeRequest.isIgnored())
                .append(MergeRequest.files.key, fileDocuments);
    }

    public List<MergeRequestDtoDb> getMergeRequests(List<String> mergeRequestIds) {
        List<MergeRequestDtoDb> mergeRequests = new ArrayList<>();
        for (String presentMergeRequestId : mergeRequestIds) {
            Optional<MergeRequestDtoDb> mergeRequest = getMergeRequest(presentMergeRequestId);
            mergeRequest.ifPresent(mergeRequests::add);
        }
        return mergeRequests;
    }

    private Optional<MergeRequestDtoDb> getMergeRequest(String mergeRequestId) {
        Document mergeRequestDoc = mergeRequestCollection.find(eq(MergeRequest.mergeRequestId.key, mergeRequestId)).first();
        return Optional.ofNullable(docToDto(mergeRequestDoc));
    }

    private MergeRequestDtoDb docToDto(Document doc) {
        if (doc == null) {
            return null;
        }

        return new MergeRequestDtoDb()
                .setMergeRequestId(doc.getInteger(MergeRequest.mergeRequestId.key))
                .setTitle(doc.getString(MergeRequest.title.key))
                .setAuthor(doc.getString(MergeRequest.author.key))
                .setUserId(doc.getInteger(MergeRequest.userId.key))
                .setDescription(doc.getString(MergeRequest.description.key))
                .setTime(doc.getLong(MergeRequest.time.key))
                .setWebUrl(doc.getString(MergeRequest.webUrl.key))
                .setSolo(doc.getBoolean(MergeRequest.isIgnored.key))
                .setCommits(getCommitsFromCachedMergeRequest(doc))
                .setCommitterEmails(new HashSet<>(doc.getList(MergeRequest.committerEmails.key, String.class)))
                .setSumOfCommitsScore(doc.getDouble(MergeRequest.sumOfCommitsScore.key))
                .setIgnored(doc.getBoolean(MergeRequest.isIgnored.key))
                .setFiles(fileRepo.getFilesFromCache(doc));
    }

    private List<CommitDtoDb> getCommitsFromCachedMergeRequest(Document doc) {
        List<Document> commitDocuments = doc.getList(MergeRequest.commits.key, Document.class);
        List<CommitDtoDb> commits = new ArrayList<>();
        for (Document presentDocument : commitDocuments) {
            commits.add(commitRepo.getCommitFromDocument(presentDocument));
        }
        return commits;
    }

    public void updateCommitUserId(String projectUrl, String commitId, Integer userId) {
        mergeRequestCollection.updateOne(
                and(eq("projectUrl", projectUrl), eq("commits.commitId", commitId)),
                set("commits.$.userId", userId));
    }
}

