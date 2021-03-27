package ca.sfu.orcus.gitlabanalyzer.committer;

import java.util.List;
import java.util.Optional;

public interface CommitterRepository {
    Optional<List<CommitterDto>> getCommitterTableForProject(int projectId);
}
