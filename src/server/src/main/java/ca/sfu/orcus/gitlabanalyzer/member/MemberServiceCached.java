package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;

import java.util.Date;
import java.util.List;

public class MemberServiceCached implements MemberService {
    @Override
    public List<MemberDto> getAllMembers(String jwt, int projectId) {
        return null;
    }

    @Override
    public List<CommitDto> getCommitsByMemberName(String jwt, int projectId, Date since, Date until, String memberName) {
        return null;
    }

    @Override
    public List<MergeRequestDto> getMergeRequestsByMemberId(String jwt, int projectId, Date since, Date until, int memberId) {
        return null;
    }
}
