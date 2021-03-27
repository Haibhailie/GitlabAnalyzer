package ca.sfu.orcus.gitlabanalyzer.member;

import org.springframework.stereotype.Repository;

@Repository
public class MockMemberRepository implements MemberRepository {
    @Override
    public boolean projectContainsMember(int projectId, int memberId) {
        return true;
    }
}
