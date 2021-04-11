package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitDtoDb;
import ca.sfu.orcus.gitlabanalyzer.file.FileRepository;
import com.google.gson.Gson;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommitRepository {

    private static final Gson gson = new Gson();
    FileRepository fileRepository = new FileRepository();

    public CommitRepository() {
    }

    private enum Commit {
        documentId("_id"),
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

    public List<Document> getCommitDocuments(List<CommitDtoDb> commits) {
        List<Document> commitsDocument = new ArrayList<>();
        for (CommitDtoDb presentCommit : commits) {
            commitsDocument.add(generateCommitDocument(presentCommit));
        }
        return commitsDocument;
    }

    private Document generateCommitDocument(CommitDtoDb commit) {
        return new Document(Commit.documentId.key, new ObjectId().toString())
                .append(Commit.commitId.key, commit.getId())
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
                .append(Commit.files.key, fileRepository.getFileDocuments(commit.getFiles()))
                .append(Commit.isIgnored.key, commit.isIgnored());
    }

    public CommitDtoDb getCommitFromDocument(Document doc) {
        CommitDtoDb commit = new CommitDtoDb();
        commit.setId(doc.getString(Commit.commitId.key));
        commit.setTitle(doc.getString(Commit.title.key));
        commit.setMessage(doc.getString(Commit.message.key));
        commit.setAuthor(doc.getString(Commit.author.key));
        commit.setAuthorEmail(doc.getString(Commit.authorEmail.key));
        commit.setTime(doc.getLong(Commit.time.key));
        commit.setWebUrl(doc.getString(Commit.webUrl.key));
        commit.setNumAdditions(doc.getInteger(Commit.numAdditions.key));
        commit.setNumDeletions(doc.getInteger(Commit.numDeletions.key));
        commit.setTotal(doc.getInteger(Commit.total.key));
        commit.setDiffs(doc.getString(Commit.diffs.key));
        commit.setScore(doc.getDouble(Commit.score.key));
        commit.setIgnored(doc.getBoolean(Commit.isIgnored.key));
        commit.setFiles(fileRepository.getFilesFromCache(doc));
        return commit;
    }
}
