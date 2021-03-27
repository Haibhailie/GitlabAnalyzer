package ca.sfu.orcus.gitlabanalyzer.member;

import org.springframework.stereotype.Repository;

@Repository("mockMemberRepo")
public class MockMemberRepository implements MemberRepository {
    @Override
    public boolean projectContainsMember(int projectId, int memberId) {
        return true;
    }
}
