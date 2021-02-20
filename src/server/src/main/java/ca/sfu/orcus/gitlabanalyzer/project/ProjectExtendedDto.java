package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.member.MemberDTO;
import org.gitlab4j.api.models.Project;

import java.util.List;

public class ProjectExtendedDto {
    private int id;
    private String name;
    private List<MemberDTO> members;
    private long numBranches;
    private long numCommits;
    private long repoSize;
    private long createdAt;

    public ProjectExtendedDto(Project project, List<MemberDTO> memberDtos, long numBranches) {
        setId(project.getId());
        setName(project.getName());
        setMembers(memberDtos);
        setNumBranches(numBranches);
        setNumCommits(project.getStatistics().getCommitCount());
        setRepoSize(project.getStatistics().getRepositorySize());
        setCreatedAt(project.getCreatedAt().getTime());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<MemberDTO> members) {
        this.members = members;
    }

    public void setNumBranches(long numBranches) {
        this.numBranches = numBranches;
    }

    public void setNumCommits(long numCommits) {
        this.numCommits = numCommits;
    }

    public void setRepoSize(long repoSize) {
        this.repoSize = repoSize;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
