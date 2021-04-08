package ca.sfu.orcus.gitlabanalyzer.member;

import java.util.List;

public interface MemberService {
    List<MemberDto> getAllMembers(String jwt, int projectId);
}
