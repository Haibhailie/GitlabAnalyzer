package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import org.bson.types.ObjectId;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AnalysisRepository {
    // Cache the given projectDto
    public void cacheProjectDto(ProjectDtoDb projectDto) {
        return;
    }

    // Cache all the memberDtos
    public void cacheMemberDtos(String projectUrl, List<MemberDtoDb> memberDtos) {
        return;
    }

    // Cache all merge request DTOs and return a list of mappings from mergeRequestId -> documentId
    public List<Pair<Integer, ObjectId>> cacheMergeRequestsDtos(String projectUrl,
                                                                List<MergeRequestDtoDb> mergeRequestDtos) {
        return Collections.emptyList();
    }


}
