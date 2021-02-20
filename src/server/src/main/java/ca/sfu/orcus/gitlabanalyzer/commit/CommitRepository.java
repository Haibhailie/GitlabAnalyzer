package ca.sfu.orcus.gitlabanalyzer.commit;

import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.stereotype.Repository;


@Repository
public class CommitRepository {
    MongoCollection<Document> collection;

    public CommitRepository() {
    }

}
