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
        List<String> documentIds = new ArrayList<>();
        for (MemberDto member : allMembers) {
            String documentId = storeMember(member);
            documentIds.add(documentId);
        }
        return documentIds;
    }

    private String storeMember(MemberDto member) {
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
