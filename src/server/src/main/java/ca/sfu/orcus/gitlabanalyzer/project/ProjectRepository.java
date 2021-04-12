package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.CommitterDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.committer.CommitterRepository;
import ca.sfu.orcus.gitlabanalyzer.utils.VariableDecoderUtil;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;

@Repository
public class ProjectRepository {
    private final MongoCollection<Document> projectsCollection;
    private final CommitterRepository committerRepo;
    private final Gson gson = new Gson();

    public ProjectRepository(CommitterRepository committerRepo) {
        MongoClient mongoClient = MongoClients.create(VariableDecoderUtil.decode("MONGO_URI"));
        MongoDatabase database = mongoClient.getDatabase(VariableDecoderUtil.decode("DATABASE"));
        projectsCollection = database.getCollection(VariableDecoderUtil.decode("PROJECTS_COLLECTION"));
        this.committerRepo = committerRepo;
    }

    private enum Project {
        documentId("_id"),
        projectId("projectId"),
        projectName("projectName"),
        projectUrl("projectUrl"),
        lastAnalysisTime("lastAnalysisTime"),
        createdAt("createdAt"),
        committers("committers");

        public final String key;

        Project(String key) {
            this.key = key;
        }
    }

    public void cacheProject(ProjectDtoDb project) {
        Document existingProject = getPartialProjectDocument(project.getWebUrl(), Project.documentId.key);
        if (existingProject != null) {
            String documentId = existingProject.getString(Project.documentId.key);
            replaceProjectDocument(documentId, project);
        } else {
            cacheNewProject(project);
        }
    }

    private void replaceProjectDocument(String documentId, ProjectDtoDb project) {
        Document projectDoc = generateProjectDocument(project, documentId);
        projectsCollection.replaceOne(getProjectEqualityParameter(project.getWebUrl()), projectDoc);
    }

    private void cacheNewProject(ProjectDtoDb project) {
        Document projectDoc = generateProjectDocument(project, new ObjectId().toString());
        projectsCollection.insertOne(projectDoc);
    }

    private Bson getProjectEqualityParameter(String projectUrl) {
        return eq(Project.projectUrl.key, projectUrl);
    }

    public boolean projectIsAlreadyCached(String projectUrl) {
        Document project = getPartialProjectDocument(projectUrl, Project.projectId.key);
        return (project != null);
    }

    private Document generateProjectDocument(ProjectDtoDb project, String documentId) {
        List<Document> committerDocs = committerRepo.getCommitterDocs(project.getCommitters());
        return new Document(Project.documentId.key, documentId)
                    .append(Project.projectId.key, project.getId())
                    .append(Project.projectName.key, project.getName())
                    .append(Project.lastAnalysisTime.key, project.getLastAnalysisTime())
                    .append(Project.createdAt.key, project.getCreatedAt())
                    .append(Project.projectUrl.key, project.getWebUrl())
                    .append(Project.committers.key, committerDocs);
    }

    public long getLastAnalysisTimeForProject(String projectUrl) {
        Document project = getPartialProjectDocument(projectUrl, Project.lastAnalysisTime.key);
        return project == null ? 0 : project.getLong(Project.lastAnalysisTime.key);
    }

    private Document getPartialProjectDocument(String projectUrl, String... projectionKey) {
        return projectsCollection.find(getProjectEqualityParameter(projectUrl))
                .projection(include(projectionKey)).first();
    }

    public Optional<ProjectDtoDb> getProject(String projectUrl) {
        Document projectDoc = projectsCollection.find(getProjectEqualityParameter(projectUrl)).first();
        return Optional.ofNullable(docToDto(projectDoc));
    }

    private ProjectDtoDb docToDto(Document projectDoc) {
        if (projectDoc == null) {
            return null;
        }
        return new ProjectDtoDb()
            .setId(projectDoc.getInteger(Project.projectId.key))
            .setName(projectDoc.getString(Project.projectName.key))
            .setWebUrl(projectDoc.getString(Project.projectUrl.key))
            .setLastAnalysisTime(projectDoc.getLong(Project.lastAnalysisTime.key))
            .setCreatedAt(projectDoc.getLong(Project.createdAt.key))
            .setCommitters(committerRepo.getCommittersFromProjectDoc(projectDoc));
    }

    public void updateCommittersMemberDto(String projectUrl, String committerEmail, MemberDtoDb memberDto) {
        String memberJson = gson.toJson(memberDto);

        // TODO: Use Committer enum in CommitterRepository to specify the keys here
        projectsCollection.updateOne(
                and(eq(Project.projectUrl.key, projectUrl), eq(Project.committers.key + ".email", committerEmail)),
                set(Project.committers.key + ".$.member", memberJson));
    }

    public Optional<CommitterDtoDb> getCommitter(String projectUrl, String committerEmail) {
        Document projectDoc = getPartialProjectDocument(projectUrl, Project.committers.key);
        return committerRepo.getCommitterFromProjectDoc(projectDoc, committerEmail);
    }
}
