package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Repository
public class ProjectRepository {
    private final MongoCollection<Document> projectsCollection;

    public ProjectRepository() {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        projectsCollection = database.getCollection(VariableDecoderUtil.decode("PROJECTS_COLLECTION"));
    }

    private enum Project {
        documentId("_id"),
        projectId("projectId"),
        isAnalyzed("isAnalyzed"),
        repoUrl("repoUrl");

        public final String key;

        Project(String key) {
            this.key = key;
        }
    }

    public boolean projectIsPublic(int projectId, String repoUrl) {
        return true;
    }

    public boolean isProjectAnalyzed(int projectId, String repoUrl) {
        Document project = projectsCollection.find(and(eq(Project.projectId.key, projectId), eq(Project.repoUrl.key, repoUrl))).first();
        return project != null && project.getBoolean(Project.isAnalyzed.key, false);
    }
}
