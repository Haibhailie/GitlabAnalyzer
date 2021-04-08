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
import java.util.ArrayList;
import java.util.List;

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
        projectUrl("projectUrl"),
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
        if (!projectIsAlreadyCached(projectDto.getId(), projectDto.getWebUrl())) {
            Document projectSkeleton = generateProjectDocument(projectDto, (visibility == Visibility.PUBLIC));
            projectsCollection.insertOne(projectSkeleton);
        }
    }

    private boolean projectIsAlreadyCached(int projectId, String repoUrl) {
        Document project = getPartialProjectDocument(projectId, repoUrl, Project.projectId.key);
        return (project != null);
    }

    private Document generateProjectDocument(ProjectDto projectDto, boolean isPublic) {
        int projectId = projectDto.getId();
        String repoUrl = projectDto.getWebUrl();
        long lastAnalysisTime = projectDto.getLastAnalysisTime();
        return new Document(Project.documentId.key, new ObjectId().toString())
                    .append(Project.projectId.key, projectId)
                    .append(Project.projectUrl.key, repoUrl)
                    .append(Project.lastAnalysisTime.key, lastAnalysisTime)
                    .append(Project.isPublic.key, isPublic);
    }

    public boolean projectIsPublic(int projectId, String projectUrl) throws NotFoundException {
        Document project = getPartialProjectDocument(projectId, projectUrl, Project.isPublic.key);

        if (project == null) {
            throw new NotFoundException("Project is not in database");
        }
        return project.getBoolean(Project.isPublic.key);
    }

    public long getLastAnalysisTimeForProject(int projectId, String projectUrl) {
        Document project = getPartialProjectDocument(projectId, projectUrl, Project.lastAnalysisTime.key);
        return project == null ? 0 : project.getLong(Project.lastAnalysisTime.key);
    }

    public List<String> getMemberDocIds(int projectId, String projectUrl) {
        Document project = getPartialProjectDocument(projectId, projectUrl, Project.memberDocumentRefs.key);
        List<String> memberDocIds = project.getList(Project.memberDocumentRefs.key, String.class);
        return memberDocIds == null ? new ArrayList<>() :
                project.getList(Project.memberDocumentRefs.key, String.class);
    }

    private Document getPartialProjectDocument(int projectId, String repoUrl, String projectionKey) {
        return projectsCollection.find(and(eq(Project.projectId.key, projectId),
                eq(Project.projectUrl.key, repoUrl)))
                .projection(include(projectionKey)).first();
    }
}
