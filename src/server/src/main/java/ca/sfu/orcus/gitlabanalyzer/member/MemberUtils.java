package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public final class MemberUtils {
    private MemberUtils() {
        throw new AssertionError();
    }

    public static final MemberDtoDb EmptyMember = new MemberDtoDb()
            .setId(0)
            .setDisplayName("")
            .setUsername("")
            .setRole("")
            .setWebUrl("")
            .setCommitterEmails(new HashSet<>())
            .setMergeRequestDocIds(new HashSet<>())
            .setNotes(new ArrayList<>());

    private static final Map<Integer, String> AccessLevelToRoleMap = Map.of(
            -1, "INVALID",
            0, "NONE",
            10, "GUEST",
            20, "REPORTER",
            30, "DEVELOPER",
            40, "MAINTAINER",
            50, "OWNER"
    );

    public static String getMemberRoleFromAccessLevel(Integer accessLevel) {
        return AccessLevelToRoleMap.getOrDefault(accessLevel, "Invalid access");
    }
}
