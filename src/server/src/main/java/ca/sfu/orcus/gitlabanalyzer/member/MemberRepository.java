package ca.sfu.orcus.gitlabanalyzer.member;

public interface MemberRepository {

    boolean projectContainsMember(int projectId, int memberId);
}
