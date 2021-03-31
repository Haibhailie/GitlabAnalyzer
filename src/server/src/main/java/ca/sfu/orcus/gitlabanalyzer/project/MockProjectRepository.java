package ca.sfu.orcus.gitlabanalyzer.project;

import org.springframework.stereotype.Repository;

@Repository("mockProjectRepo")
public class MockProjectRepository implements ProjectRepository {
    @Override
    public boolean projectIsPublic(int projectId) {
        return true;
    }
}
