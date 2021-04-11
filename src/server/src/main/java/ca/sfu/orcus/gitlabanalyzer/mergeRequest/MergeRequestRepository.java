package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Repository
public class MergeRequestRepository {

    private final MongoCollection<Document> mergeRequestCollection;
    private static final Gson gson = new Gson();

    private enum File {
        name("name"),
        extension("extension"),
        fileScore("fileScore"),
        linesOfCodeChanges("linesOfCodeChanges"),
        isIgnored("isIgnored");

        public String key;

        File(String key) {
            this.key = key;
        }
    }

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

    private enum Commit {
        commitId("commitId"),
        title("title"),
        message("message"),
        author("author"),
        authorEmail("authorEmail"),
        time("time"),
        webUrl("webUrl"),
        numAdditions("numAdditions"),
        numDeletions("numDeletions"),
        total("numTotal"),
        diffs("diffs"),
        isIgnored("isIgnored"),
        files("files"),
        score("score");

        public String key;

        Commit(String key) {
            this.key = key;
        }

    }

    public MergeRequestRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        mergeRequestCollection = database.getCollection(VariableDecoderUtil.decode("MERGE_REQUEST_COLLECTION"));
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
                .append(MergeRequest.commits.key, getCommitDocuments(mergeRequest.getCommits()))
                .append(MergeRequest.files.key, getFileDocuments(mergeRequest.getFiles()))
                .append(MergeRequest.isIgnored.key, mergeRequest.isIgnored());
    }

    private Document generateCommitDocument(CommitDtoDb commit) {
        Document commitsDocument = new Document();
        commitsDocument.append(Commit.commitId.key, commit.getId())
                .append(Commit.title.key, commit.getTitle())
                .append(Commit.message.key, commit.getMessage())
                .append(Commit.author.key, commit.getAuthor())
                .append(Commit.authorEmail.key, commit.getAuthorEmail())
                .append(Commit.time.key, commit.getTime())
                .append(Commit.webUrl.key, commit.getWebUrl())
                .append(Commit.numAdditions.key, commit.getNumAdditions())
                .append(Commit.numDeletions.key, commit.getNumDeletions())
                .append(Commit.total.key, commit.getTotal())
                .append(Commit.diffs.key, commit.getDiffs())
                .append(Commit.score.key, commit.getScore())
                .append(Commit.files.key, getFileDocuments(commit.getFiles()))
                .append(Commit.isIgnored.key, commit.isIgnored());

        return commitsDocument;
    }

    private List<Document> getFileDocuments(List<FileDto> files) {
        List<Document> fileDocument = new ArrayList<>();
        for (FileDto presentFile : files) {
            fileDocument.add(generateFileDocuments(presentFile));
        }
        return fileDocument;
    }

    private Document generateFileDocuments(FileDto file) {
        Document fileDocument = new Document();
        fileDocument.append(File.name.key, file.getFileName())
                .append(File.extension.key, file.getFileExtension())
                .append(File.fileScore.key, file.getFileScore())
                .append(File.linesOfCodeChanges.key, file.getLinesOfCodeChanges())
                .append(File.isIgnored.key, file.isIgnored());
        return fileDocument;
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
        MergeRequestDtoDb mergeRequest = new MergeRequestDtoDb();
        mergeRequest.setMergeRequestId(doc.getInteger(MergeRequest.mergeRequestId.key));
        mergeRequest.setTitle(doc.getString(MergeRequest.title.key));
        mergeRequest.setAuthor(doc.getString(MergeRequest.author.key));
        mergeRequest.setAuthorId(doc.getInteger(MergeRequest.authorId.key));
        mergeRequest.setDescription(doc.getString(MergeRequest.description.key));
        mergeRequest.setTime(doc.getLong(MergeRequest.time.key));
        mergeRequest.setWebUrl(doc.getString(MergeRequest.webUrl.key));
        mergeRequest.setSumOfCommitsScore(doc.getDouble(MergeRequest.sumOfCommitsScore.key));
        mergeRequest.setIgnored(doc.getBoolean(MergeRequest.isIgnored.key));
        mergeRequest.setCommitterNames(new HashSet<>(doc.getList(MergeRequest.committerNames.key, String.class)));

        mergeRequest.setCommits(doc.getList(MergeRequest.commits.key, CommitDtoDb.class));
        mergeRequest.setFiles(doc.getList(MergeRequest.files.key, FileDto.class));

        return mergeRequest;
    }

}

