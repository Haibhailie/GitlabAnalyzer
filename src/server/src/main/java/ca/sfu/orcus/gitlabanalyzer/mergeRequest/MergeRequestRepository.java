package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
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
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Repository
public class MergeRequestRepository {

    private final MongoCollection<Document> mergeRequestCollection;

    private enum MergeRequest {
        mergeRequestId("mergeRequestId"),
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
            String documentId = cacheMergeRequest(presentMergeRequest, projectUrl);
            documentIds.add(documentId);
        }
        return documentIds;
    }

    private String cacheMergeRequest(MergeRequestDtoDb mergeRequest, String projectUrl) {
        String documentId = new ObjectId().toString();
        Document mergeRequestDocument = generateMergeRequestDocument(mergeRequest, documentId, projectUrl);
        if (mergeRequestCollection.findOneAndReplace(getMergeRequestEqualityParameter(projectUrl, mergeRequest), mergeRequestDocument) == null) {
            mergeRequestCollection.insertOne(mergeRequestDocument);
        }
        return documentId;
    }

    private Bson getMergeRequestEqualityParameter(String projectUrl, MergeRequestDtoDb mergeRequest) {
        return and(eq(MergeRequest.projectUrl.key, projectUrl), eq(MergeRequest.mergeRequestId.key, mergeRequest.getMergeRequestId()));
    }

    private Document generateMergeRequestDocument(MergeRequestDtoDb mergeRequest, String documentId, String projectUrl) {
        return new Document(MergeRequest.mergeRequestId.key, documentId)
                .append(MergeRequest.projectUrl.key, projectUrl)
                .append(MergeRequest.title.key, mergeRequest.getTitle())
                .append(MergeRequest.author.key, mergeRequest.getAuthor())
                .append(MergeRequest.authorId.key, mergeRequest.getAuthorId())
                .append(MergeRequest.description.key, mergeRequest.getDescription())
                .append(MergeRequest.webUrl.key, mergeRequest.getWebUrl())
                .append(MergeRequest.sumOfCommitsScore.key, mergeRequest.getSumOfCommitsScore())
                .append(MergeRequest.committerNames.key, mergeRequest.getCommitterNames())
                .append(MergeRequest.commits.key, generateCommitDocuments(mergeRequest))
                .append(MergeRequest.files.key, generateFileDocuments(mergeRequest))
                .append(MergeRequest.isIgnored.key, mergeRequest.isIgnored());
    }

    private Document generateCommitDocuments(MergeRequestDtoDb mergeRequest) {
        Document commitsDocument = new Document();
        List<CommitDtoDb> commits = mergeRequest.getCommits();
        for (CommitDtoDb presentCommit : commits) {
            commitsDocument.append(MergeRequest.commits.key, presentCommit);
        }
        return commitsDocument;
    }

    private Document generateFileDocuments(MergeRequestDtoDb mergeRequest) {
        Document filesDocument = new Document();
        List<FileDto> fileDtos = mergeRequest.getFiles();
        for (FileDto fileDto : fileDtos) {
            filesDocument.append(MergeRequest.files.key, fileDto);
        }
        return filesDocument;
    }



}

