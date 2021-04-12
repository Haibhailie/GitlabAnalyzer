package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitterDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberMock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.*;

// TODO: When this is deleted, remember to move MemberMock back into the test package

@Repository("mockCommitterRepo")
public class CommitterRepository {
    private static final Gson gson = new Gson();

    private enum Committer {
        documentId("_id"),
        email("email"),
        name("name"),
        commitIds("commitIds"),
        mergeRequestIds("mergeRequestIds"),
        member("member"),
        committers("committers");

        private final String key;

        Committer(String key) {
            this.key = key;
        }
    }

    public void updateCommitters(int projectId, Map<String, Integer> committerToMemberMap) {
        // do nothing
    }

    public List<Document> getCommitterDocs(List<CommitterDtoDb> committerDtos) {
        List<Document> committerDocs = new ArrayList<>();
        for (CommitterDtoDb committer : committerDtos) {
            committerDocs.add(generateCommitterDocument(committer));
        }
        return committerDocs;
    }

    private Document generateCommitterDocument(CommitterDtoDb committer) {
        return new Document(Committer.documentId.key, new ObjectId().toString())
                .append(Committer.email.key, committer.getEmail())
                .append(Committer.name.key, committer.getName())
                .append(Committer.commitIds.key, gson.toJson(committer.getCommitIds()))
                .append(Committer.mergeRequestIds.key, gson.toJson(committer.getMergeRequestIds()))
                .append(Committer.member.key, gson.toJson(committer.getMember()));
    }

    public List<CommitterDtoDb> getCommittersFromProjectDoc(Document doc) {
        List<Document> committerDoc = doc.getList(Committer.committers.key, Document.class);
        List<CommitterDtoDb> committers = new ArrayList<>();
        for (Document d : committerDoc) {
            committers.add(docToDto(d));
        }
        return committers;
    }

    public Optional<CommitterDtoDb> getCommitterFromProjectDoc(Document doc, String committerEmail) {
        List<Document> committerDoc = doc.getList(Committer.committers.key, Document.class);
        for (Document d : committerDoc) {
            if (d.getString(Committer.email.key).equals(committerEmail)) {
                return Optional.of(docToDto(d));
            }
        }
        return Optional.empty();
    }

    private CommitterDtoDb docToDto(Document committerDoc) {
        if (committerDoc == null) {
            return null;
        }
        return new CommitterDtoDb()
                .setEmail(committerDoc.getString(Committer.email.key))
                .setName(committerDoc.getString(Committer.name.key))
                .setCommitIds(gson.fromJson(committerDoc.getString(Committer.commitIds.key), new TypeToken<HashSet<String>>(){}.getType()))
                .setMergeRequestIds(gson.fromJson(committerDoc.getString(Committer.mergeRequestIds.key), new TypeToken<HashSet<Integer>>(){}.getType()))
                .setMember(gson.fromJson(committerDoc.getString(Committer.member.key), MemberDtoDb.class));
    }
}
