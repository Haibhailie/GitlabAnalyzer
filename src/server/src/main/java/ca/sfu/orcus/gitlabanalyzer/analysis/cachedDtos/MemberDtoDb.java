package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;

import java.util.*;

public final class MemberDtoDb {
    private int id;
    private String displayName;
    private String username;
    private String role;
    private String webUrl;

    private Set<String> committerEmails;
    private Set<Integer> commitsToMaster;
    private Set<Integer> mergeRequestIds;
    private List<NoteDtoDb> notes;

    public MemberDtoDb(Member member) {
        this(member,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new ArrayList<>());
    }

    public MemberDtoDb(Member member,
                       Set<String> committerEmails,
                       Set<Integer> commitsToMaster,
                       Set<Integer> mergeRequestIds,
                       List<NoteDtoDb> notes) {
        setId(member.getId());
        setDisplayName(member.getName());
        setUsername(member.getUsername());
        setRole(member.getAccessLevel());
        setWebUrl(member.getWebUrl());

        setCommitterEmails(committerEmails);
        setCommitsToMaster(commitsToMaster);
        setMergeRequestIds(mergeRequestIds);
        setNotes(notes);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public void setCommitterEmails(Set<String> committerEmails) {
        this.committerEmails = committerEmails;
    }

    public void setCommitsToMaster(Set<Integer> commitsToMaster) {
        this.commitsToMaster = commitsToMaster;
    }

    public void setMergeRequestIds(Set<Integer> mergeRequestIds) {
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
                && this.displayName.equals(m.displayName)
                && this.username.equals(m.username)
                && this.role.equals(m.role)
                && this.webUrl.equals(m.webUrl)
                && this.committerEmails.equals(m.committerEmails)
                && this.commitsToMaster.equals(m.commitsToMaster)
                && this.mergeRequestIds.equals(m.mergeRequestIds)
                && this.notes.equals(m.notes));
    }
}