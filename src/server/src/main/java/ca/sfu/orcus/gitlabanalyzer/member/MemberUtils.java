package ca.sfu.orcus.gitlabanalyzer.member;

import java.util.Map;

public class MemberUtils {
    private MemberUtils() {
        throw new AssertionError();
    }

    private static final Map<Integer, String> AccessLevelToRoleMap = Map.of(
            0, "No access",
            5, "Minimal access",
            10, "Guest",
            20, "Reporter",
            30, "Developer",
            40, "Maintainer",
            50, "Owner"
    );

    public static String getMemberRoleFromAccessLevel(Integer accessLevel) {
        return AccessLevelToRoleMap.getOrDefault(accessLevel, "Invalid access");
    }
}
