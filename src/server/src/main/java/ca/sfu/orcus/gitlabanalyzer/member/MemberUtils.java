package ca.sfu.orcus.gitlabanalyzer.member;

import org.gitlab4j.api.models.AccessLevel;

import java.util.HashMap;
import java.util.Map;

public final class MemberUtils {
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

    private static final Map<String, AccessLevel> RoleToAccessLevel = invertMap();

    private static Map<String, AccessLevel> invertMap() {
        Map<String, AccessLevel> inverted = new HashMap<>();
        for (Map.Entry<Integer, String> entry : AccessLevelToRoleMap.entrySet()) {
            inverted.put(entry.getValue(), AccessLevel.forValue(entry.getKey()));
        }
        return inverted;
    }

    public static String getMemberRoleFromAccessLevel(Integer accessLevel) {
        return AccessLevelToRoleMap.getOrDefault(accessLevel, "Invalid access");
    }

    public static AccessLevel getAccessLevelFromMemberRole(String accessLevel) {
        return RoleToAccessLevel.getOrDefault(accessLevel, AccessLevel.INVALID);
    }
}
