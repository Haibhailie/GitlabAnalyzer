package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.NoteDtoDb;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MemberRepository {
    private final MongoCollection<Document> memberCollection;
    private static final Gson gson = new Gson();

    private enum Member {
        documentId("_id"),
        projectUrl("projectUrl"),
        memberId("memberId"),
        displayName("displayName"),
        username("username"),
        role("role"),
        memberUrl("memberUrl"),
        committerEmails("committerEmails"),
        mergeRequestDocIds("mergeRequestDocIds"),
        notes("notes");

        public String key;

        Member(String key) {
            this.key = key;
        }
    }

    public MemberRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        memberCollection = database.getCollection(VariableDecoderUtil.decode("MEMBERS_COLLECTION"));
    }

    public List<String> cacheAllMembers(String projectUrl, List<MemberDtoDb> allMembers) {
        List<String> documentIds = new ArrayList<>();
        for (MemberDtoDb member : allMembers) {
            if (!memberIsAlreadyCached(member, projectUrl)) {
                String documentId = cacheMember(member, projectUrl);
                documentIds.add(documentId);
            }
        }
        return documentIds;
    }

    private boolean memberIsAlreadyCached(MemberDtoDb member, String projectUrl) {
        Document memberDoc = memberCollection.find(and(eq(Member.memberId.key, member.getId()),
                                                    eq(Member.projectUrl.key, projectUrl)))
                                                    .projection(include(Member.memberId.key)).first();
        return (memberDoc != null);
    }

    private String cacheMember(MemberDtoDb member, String projectUrl) {
        String documentId = new ObjectId().toString();
        Document memberDocument = generateMemberDocument(member, documentId, projectUrl);
        memberCollection.insertOne(memberDocument);
        return documentId;
    }

    private Document generateMemberDocument(MemberDtoDb member, String documentId, String projectUrl) {
        return new Document(Member.documentId.key, documentId)
                    .append(Member.projectUrl.key, projectUrl)
                    .append(Member.memberId.key, member.getId())
                    .append(Member.displayName.key, member.getDisplayName())
                    .append(Member.username.key, member.getUsername())
                    .append(Member.role.key, member.getRole())
                    .append(Member.memberUrl.key, member.getWebUrl())
                    .append(Member.committerEmails.key, member.getCommitterEmails())
                    .append(Member.mergeRequestDocIds.key, member.getMergeRequestDocIds())
                    .append(Member.notes.key, gson.toJson(member.getNotes()));
    }

    public List<MemberDtoDb> getMembers(List<String> documentIds) {
        List<MemberDtoDb> members = new ArrayList<>();
        for (String documentId : documentIds) {
            Optional<MemberDtoDb> member = getMember(documentId);
            member.ifPresent(members::add);
        }
        return members;
    }

    private Optional<MemberDtoDb> getMember(String documentId) {
        Document memberDoc = memberCollection.find(eq(Member.documentId.key, documentId)).first();
        return Optional.ofNullable(docToDto(memberDoc));
    }

    private MemberDtoDb docToDto(Document memberDoc) {
        if (memberDoc == null) {
            return null;
        }
        MemberDtoDb member = new MemberDtoDb();
        member.setId(memberDoc.getInteger(Member.memberId));
        member.setDisplayName(memberDoc.getString(Member.displayName.key));
        member.setUsername(memberDoc.getString(Member.username.key));
        member.setRole(MemberUtils.getAccessLevelFromMemberRole(memberDoc.getString(Member.role.key)));
        member.setWebUrl(memberDoc.getString(Member.memberUrl.key));
        member.setCommitterEmails(new HashSet<>(memberDoc.getList(Member.committerEmails.key, String.class)));
        member.setMergeRequestDocIds(new HashSet<>(memberDoc.getList(Member.mergeRequestDocIds.key, ObjectId.class)));
        member.setNotes(gson.fromJson(memberDoc.getString(Member.notes.key), new ArrayList<NoteDtoDb>(){}.getClass()));
        return member;
    }
}
