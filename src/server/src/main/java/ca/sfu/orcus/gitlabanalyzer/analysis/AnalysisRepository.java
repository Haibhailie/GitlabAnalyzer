package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MergeRequestDtoDb;
import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.ProjectDtoDb;
import ca.sfu.orcus.gitlabanalyzer.member.MemberRepository;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnalysisRepository {
    private final MemberRepository memberRepo;
    private final MergeRequestRepository mergeRequestRepo;

    @Autowired
    public AnalysisRepository(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }
    
    public void cacheProjectDto(ProjectDtoDb projectDto) {
        return;
    }

    public List<String> cacheMemberDtos(String projectUrl, List<MemberDtoDb> memberDtos) {
        return memberRepo.cacheAllMembers(projectUrl, memberDtos);
    }

    public List<String> cacheMergeRequestDtos(String projectUrl, List<MergeRequestDtoDb> mergeRequests) {
        return mergeRequestRepo.cacheAllMergeRequests(projectUrl, mergeRequests);
    }
}
