package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AnalysisRepository {
    private final MemberRepository memberRepo;

    @Autowired
    public AnalysisRepository(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    // Cache the given projectDto
    public void cacheProjectDto(ProjectDtoDb projectDto) {
        return;
    }

    public List<String> cacheMemberDtos(String projectUrl, List<MemberDtoDb> memberDtos) {
        return memberRepo.cacheAllMembers(projectUrl, memberDtos);
    }

    // Cache all merge request DTOs and return a list of mappings from mergeRequestId -> documentId
    public List<Pair<Integer, ObjectId>> cacheMergeRequestsDtos(String projectUrl,
                                                                List<MergeRequestDtoDb> mergeRequestDtos) {
        return Collections.emptyList();
    }
}
