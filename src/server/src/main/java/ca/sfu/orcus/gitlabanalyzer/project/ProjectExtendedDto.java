package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.member.MemberDTO;
import org.gitlab4j.api.models.Project;

import java.util.List;

public class ProjectExtendedDto {
    private Integer id;
    private String name;
    private List<MemberDTO> members;
    private Long numBranches;
    private Long numCommits;
    private Long repoSize;

    public ProjectExtendedDto(Project project, List<MemberDTO> memberDtos, Long numBranches) {
        setId(project.getId());
        setName(project.getName());
        setMembers(memberDtos);
        setNumBranches(numBranches);
        setNumCommits(project.getStatistics().getCommitCount());
        setRepoSize(project.getStatistics().getRepositorySize());
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<MemberDTO> members) {
        this.members = members;
    }

    public void setNumBranches(Long numBranches) {
        this.numBranches = numBranches;
    }

    public void setNumCommits(Long numCommits) {
        this.numCommits = numCommits;
    }

    public void setRepoSize(Long repoSize) {
        this.repoSize = repoSize;
    }
}
