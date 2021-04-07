package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class MemberDtoDb {
    private int id;
    private String name;
    private String username;
    private String role;
    private String webUrl;

    private Set<String> committers;
    private List<Integer> commitsOnOwnMrs;
    private List<Integer> commitsOnOtherMrs;
    private List<Integer> commitsToMaster;
    private List<Integer> mergeRequestIds;
    private List<NoteDtoDb> notes;

    public MemberDtoDb(Member member) {
        this(member,
                Collections.emptySet(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public MemberDtoDb(Member member,
                       Set<String> committers,
                       List<Integer> commitsOnOwnMrs,
                       List<Integer> commitsOnOtherMrs,
                       List<Integer> commitsToMaster,
                       List<Integer> mergeRequestIds,
                       List<NoteDtoDb> notes) {
        setId(member.getId());
        setName(member.getName());
        setUsername(member.getUsername());
        setRole(member.getAccessLevel());
        setWebUrl(member.getWebUrl());

        setCommitters(committers);
        setCommitsOnOwnMrs(commitsOnOwnMrs);
        setCommitsOnOtherMrs(commitsOnOtherMrs);
        setCommitsToMaster(commitsToMaster);
        setMergeRequestIds(mergeRequestIds);
        setNotes(notes);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(AccessLevel accessLevel) {
        this.role = (accessLevel == null) ? "GUEST" : MemberUtils.getMemberRoleFromAccessLevel(accessLevel.value);
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCommitters(Set<String> committers) {
        this.committers = committers;
    }

    public void setCommitsOnOwnMrs(List<Integer> commitsOnOwnMrs) {
        this.commitsOnOwnMrs = commitsOnOwnMrs;
    }

    public void setCommitsOnOtherMrs(List<Integer> commitsOnOtherMrs) {
        this.commitsOnOtherMrs = commitsOnOtherMrs;
    }

    public void setCommitsToMaster(List<Integer> commitsToMaster) {
        this.commitsToMaster = commitsToMaster;
    }

    public void setMergeRequestIds(List<Integer> mergeRequestIds) {
        this.mergeRequestIds = mergeRequestIds;
    }

    public void setNotes(List<NoteDtoDb> comments) {
        this.notes = comments;
    }

    public void addMergeRequestId(Integer mergeRequestId) {
        this.mergeRequestIds.add(mergeRequestId);
    }

    public void addNote(NoteDtoDb note) {
        this.notes.add(note);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MemberDtoDb)) {
            return false;
        }

        MemberDtoDb m = (MemberDtoDb) o;

        return (this.id == m.id
                && this.name.equals(m.name)
                && this.username.equals(m.username)
                && this.role.equals(m.role)
                && this.webUrl.equals(webUrl)
                && this.committers.equals(m.committers)
                && this.commitsOnOtherMrs.equals(m.commitsOnOwnMrs)
                && this.commitsOnOtherMrs.equals(m.commitsOnOtherMrs)
                && this.commitsToMaster.equals(m.commitsToMaster)
                && this.mergeRequestIds.equals(m.mergeRequestIds)
                && this.notes.equals(m.notes));
    }
}
