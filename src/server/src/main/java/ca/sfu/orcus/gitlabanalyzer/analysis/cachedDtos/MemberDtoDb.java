package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import org.bson.types.ObjectId;
import org.gitlab4j.api.models.Member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MemberDtoDb {
    private int id;
    private String displayName;
    private String username;
    private String role;
    private String webUrl;

    private Set<String> committerEmails;
    private Set<ObjectId> mergeRequestDocIds;
    private List<NoteDtoDb> notes;

    public MemberDtoDb() {
        setId(-1);
        setDisplayName("-");
        setUsername("-");
        setRole("-");
        setWebUrl("-");
        setCommitterEmails(new HashSet<>());
        setMergeRequestDocIds(new HashSet<>());
        setNotes(new ArrayList<>());
    }

    public MemberDtoDb(Member member) {
        this(member,
                new HashSet<>(),
                new HashSet<>(),
                new ArrayList<>());
    }

    public MemberDtoDb(Member member,
                       Set<String> committerEmails,
                       Set<ObjectId> mergeRequestDocIds,
                       List<NoteDtoDb> notes) {
        setId(member.getId());
        setDisplayName(member.getName());
        setUsername(member.getUsername());

        setRole(member.getAccessLevel() == null ? "GUEST" : MemberUtils.getMemberRoleFromAccessLevel(member.getAccessLevel().value));
        setWebUrl(member.getWebUrl());

        setCommitterEmails(committerEmails);
        setMergeRequestDocIds(mergeRequestDocIds);
        setNotes(notes);
    }

    public MemberDtoDb setId(int id) {
        this.id = id;
        return this;
    }

    public MemberDtoDb setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public MemberDtoDb setUsername(String username) {
        this.username = username;
        return this;
    }

    public MemberDtoDb setRole(String role) {
        this.role = role;
        return this;
    }

    public MemberDtoDb setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public MemberDtoDb setCommitterEmails(Set<String> committerEmails) {
        this.committerEmails = committerEmails;
        return this;
    }

    public MemberDtoDb setMergeRequestDocIds(Set<ObjectId> mergeRequestDocIds) {
        this.mergeRequestDocIds = mergeRequestDocIds;
        return this;
    }

    public MemberDtoDb setNotes(List<NoteDtoDb> comments) {
        this.notes = comments;
        return this;
    }

    public void addMergeRequestDocId(ObjectId mergeRequestDocId) {
        this.mergeRequestDocIds.add(mergeRequestDocId);
    }

    public void addNote(NoteDtoDb note) {
        this.notes.add(note);
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Set<String> getCommitterEmails() {
        return committerEmails;
    }

    public Set<ObjectId> getMergeRequestDocIds() {
        return mergeRequestDocIds;
    }

    public List<NoteDtoDb> getNotes() {
        return notes;
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
                && this.mergeRequestDocIds.equals(m.mergeRequestDocIds)
                && this.notes.equals(m.notes));
    }
}
