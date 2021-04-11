package ca.sfu.orcus.gitlabanalyzer.authentication;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class AuthenticationRepository {
    MongoCollection<Document> collection;

    public AuthenticationRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        this.collection = database.getCollection(VariableDecoderUtil.decode("USERS_COLLECTION"));
    }

    // BSON document keys for documents to be stored in Users collection
    private enum User {
        id("_id"),
        type("type"),
        pat("pat"),
        authToken("authToken"),
        jwt("jwt");

        public final String key;

        User(String key) {
            this.key = key;
        }
    }

    public void addNewUserByPat(AuthenticationUser newUser) {
        collection.insertOne(generateUserPatDoc(newUser));
    }

    public void addNewUserByUserPass(AuthenticationUser newUser) {
        collection.insertOne(generateUserAuthTokenDoc(newUser));
    }

    private Document generateUserPatDoc(AuthenticationUser newUser) {
        return new Document(User.id.key, new ObjectId())
                .append(User.type.key, "pat")
                .append(User.pat.key, newUser.getPat())
                .append(User.jwt.key, newUser.getJwt());
    }

    private Document generateUserAuthTokenDoc(AuthenticationUser newUser) {
        return new Document(User.id.key, new ObjectId())
                .append(User.type.key, "authToken")
                .append(User.authToken.key, newUser.getAuthToken())
                .append(User.jwt.key, newUser.getJwt());
    }

    public boolean containsJwt(String jwt) {
        Document user = collection.find(eq(User.jwt.key, jwt)).first();
        return (user != null);
    }

    public String getPatFor(String jwt) {
        Document user = collection.find(eq(User.jwt.key, jwt)).first();
        return user.getString(User.pat.key);
    }

    public String getAuthTokenFor(String jwt) {
        Document user = collection.find(eq(User.jwt.key, jwt)).first();
        return user.getString(User.authToken.key);
    }

    public boolean containsPat(String pat) {
        Document user = collection.find(eq(User.pat.key, pat)).first();
        return (user != null);
    }

    public String getJwtForPat(String pat) {
        Document user = collection.find(eq(User.pat.key, pat)).first();
        return user.getString(User.jwt.key);
    }

    public boolean containsAuthToken(String authToken) {
        Document user = collection.find(eq(User.authToken.key, authToken)).first();
        return (user != null);
    }

    public String getJwtForAuthToken(String authToken) {
        Document user = collection.find(eq(User.authToken.key, authToken)).first();
        return user.getString(User.jwt.key);
    }
}
