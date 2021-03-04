package ca.sfu.orcus.gitlabanalyzer.member;

import java.util.Map;

public class MemberUtils {
    private MemberUtils() {
        throw new AssertionError();
    }

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
