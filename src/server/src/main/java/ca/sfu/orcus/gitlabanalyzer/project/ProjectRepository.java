package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.gitlab4j.api.models.Visibility;
import org.springframework.stereotype.Repository;

import javax.ws.rs.NotFoundException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

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
        repoUrl("repoUrl"),
        lastAnalysisTime("lastAnalysisTime"),
        isPublic("isPublic"),
        analysis("analysis"),
        memberDocumentRefs("memRefs");

        public final String key;

        Project(String key) {
            this.key = key;
        }
    }

    public void cacheProjectSkeleton(ProjectDto projectDto, Visibility visibility) {
        if (!projectAlreadyCached(projectDto.getId(), projectDto.getWebUrl())) {
            Document projectSkeleton = generateProjectDocument(projectDto, (visibility == Visibility.PUBLIC));
            projectsCollection.insertOne(projectSkeleton);
        }
    }

    private boolean projectAlreadyCached(int projectId, String repoUrl) {
        Document project =
                projectsCollection.find(and(eq(Project.projectId.key, projectId),
                        eq(Project.repoUrl.key, repoUrl)))
                        .projection(include(Project.projectId.key)).first();
        return (project != null);
    }

    private Document generateProjectDocument(ProjectDto projectDto, boolean isPublic) {
        int projectId = projectDto.getId();
        String repoUrl = projectDto.getWebUrl();
        long lastAnalysisTime = projectDto.getLastAnalysisTime();
        return new Document(Project.projectId.key, new ObjectId().toString())
                    .append(Project.projectId.key, projectId)
                    .append(Project.repoUrl.key, repoUrl)
                    .append(Project.lastAnalysisTime.key, lastAnalysisTime)
                    .append(Project.isPublic.key, isPublic);
    }

    public boolean projectIsPublic(int projectId, String repoUrl) throws NotFoundException {
        Document project =
                projectsCollection.find(and(eq(Project.projectId.key, projectId),
                    eq(Project.repoUrl.key, repoUrl)))
                    .projection(include(Project.isPublic.key)).first();
        if (project == null) {
            throw new NotFoundException("Project is not in database");
        }

        return project.getBoolean(project.getBoolean(Project.isPublic.key));
    }

    public long getLastAnalysisTimeForProject(int projectId, String repoUrl) {
        Document project =
                projectsCollection.find(and(eq(Project.projectId.key, projectId),
                        eq(Project.repoUrl.key, repoUrl)))
                        .projection(include(Project.lastAnalysisTime.key)).first();
        if (project == null) {
            return 0;
        } else {
            return project.getLong(Project.lastAnalysisTime.key);
        }
    }
}
