package ca.sfu.orcus.gitlabanalyzer.member;

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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MemberRepository {
    private final MongoCollection<Document> memberCollection;

    private enum Member {
        documentId("_id"),
        displayName("displayName"),
        memberId("memberId"),
        username("username"),
        role("role"),
        repoUrl("repoUrl");

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

    public List<String> cacheAllMembers(List<MemberDto> allMembers) {
        System.out.println("caching members...");
        List<String> documentIds = new ArrayList<>();
        for (MemberDto member : allMembers) {
            if (!memberIsAlreadyCached(member)) {
                String documentId = cacheMember(member);
                documentIds.add(documentId);
            }
        }
        return documentIds;
    }

    private boolean memberIsAlreadyCached(MemberDto member) {
        Document memberDoc = memberCollection.find(and(eq(Member.memberId.key, member.getId()),
                                                    eq(Member.repoUrl.key, member.getWebUrl())))
                                                    .projection(include(Member.memberId.key)).first();
        return (memberDoc != null);
    }

    private String cacheMember(MemberDto member) {
        String documentId = new ObjectId().toString();
        Document memberDocument = generateMemberDocument(member, documentId);
        memberCollection.insertOne(memberDocument);
        return documentId;
    }

    private Document generateMemberDocument(MemberDto member, String documentId) {
        return new Document(Member.documentId.key, documentId)
                    .append(Member.displayName.key, member.getDisplayName())
                    .append(Member.memberId.key, member.getId())
                    .append(Member.username.key, member.getUsername())
                    .append(Member.role.key, member.getRole())
                    .append(Member.repoUrl.key, member.getWebUrl());
    }

    public boolean projectContainsMember(int projectId, int memberId) {
        return true;
    }
}
