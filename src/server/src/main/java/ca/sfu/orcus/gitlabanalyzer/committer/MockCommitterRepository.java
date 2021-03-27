package ca.sfu.orcus.gitlabanalyzer.committer;

import ca.sfu.orcus.gitlabanalyzer.member.MemberDto;
import ca.sfu.orcus.gitlabanalyzer.member.MemberMock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("mockCommitterRepo")
public class MockCommitterRepository implements CommitterRepository {

    @Override
    public Optional<List<CommitterDto>> getCommitterTableForProject(int projectId) {
        List<CommitterDto> list = generateCommitterTable();
        return Optional.of(list);
    }

    private List<CommitterDto> generateCommitterTable() {
        MemberDto memberDto = new MemberDto(MemberMock.createMember());
        return List.of(new CommitterDto("example@domain.com", "Example User", memberDto));
    }
}
