package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.ArrayList;

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
        projectName("projectName"),
        projectUrl("projectUrl"),
        lastAnalysisTime("lastAnalysisTime"),
        createdAt("createdAt"),
        analysis("analysis"),
        committers("committers");

        public final String key;

        Project(String key) {
            this.key = key;
        }
    }

    public void cacheProject(ProjectDtoDb project) {
        Document existingProject = getPartialProjectDocument(project.getId(), project.getWebUrl(), Project.documentId.key);
        if (existingProject != null) {
            String documentId = existingProject.getString(Project.documentId.key);
            replaceProjectDocument(documentId, project);
        } else {
            cacheNewProject(project);
        }
    }

    private void replaceProjectDocument(String documentId, ProjectDtoDb project) {
        Document projectDoc = generateProjectDocument(project, documentId);
        projectsCollection.replaceOne(getProjectEqualityParameter(project.getId(), project.getWebUrl()), projectDoc);
    }

    private void cacheNewProject(ProjectDtoDb project) {
        Document projectDoc = generateProjectDocument(project, new ObjectId().toString());
        projectsCollection.insertOne(projectDoc);
    }

    private Bson getProjectEqualityParameter(int projectId, String projectUrl) {
        return and(eq(Project.projectId.key, projectId), eq(Project.projectUrl.key, projectUrl));
    }

    private boolean projectIsAlreadyCached(int projectId, String projectUrl) {
        Document project = getPartialProjectDocument(projectId, projectUrl, Project.projectId.key);
        return (project != null);
    }

    private Document generateProjectDocument(ProjectDtoDb project, String documentId) {
        return new Document(Project.documentId.key, documentId)
                    .append(Project.projectId.key, project.getId())
                    .append(Project.projectName.key, project.getName())
                    .append(Project.lastAnalysisTime.key, project.getLastAnalysisTime())
                    .append(Project.createdAt.key, project.getCreatedAt())
                    .append(Project.projectUrl.key, project.getWebUrl())
                    .append(Project.committers.key, project.getCommitters());
    }

    public long getLastAnalysisTimeForProject(int projectId, String projectUrl) {
        Document project = getPartialProjectDocument(projectId, projectUrl, Project.lastAnalysisTime.key);
        return project == null ? 0 : project.getLong(Project.lastAnalysisTime.key);
    }

    private Document getPartialProjectDocument(int projectId, String repoUrl, String projectionKey) {
        return projectsCollection.find(and(eq(Project.projectId.key, projectId),
                eq(Project.projectUrl.key, repoUrl)))
                .projection(include(projectionKey)).first();
    }
}
