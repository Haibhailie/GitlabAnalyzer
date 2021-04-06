package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;

import java.util.Date;
import java.util.List;

public interface MemberService {
    List<MemberDto> getAllMembers(String jwt, int projectId);

    List<CommitDto> getCommitsByMemberName(String jwt,
                                           int projectId,
                                           Date since,
                                           Date until,
                                           String memberName);

    List<MergeRequestDto> getMergeRequestsByMemberId(String jwt,
                                                     int projectId,
                                                     Date since,
                                                     Date until,
                                                     int memberId);
}
