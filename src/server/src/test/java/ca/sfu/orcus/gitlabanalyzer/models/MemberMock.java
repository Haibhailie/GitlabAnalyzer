package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;

import java.util.Random;

public class MemberMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;
    private static final AccessLevel[] accessLevels = AccessLevel.values();

    public static final int defaultId = rand.nextInt(upperBound);
    public static final AccessLevel defaultAccessLevel = accessLevels[rand.nextInt(accessLevels.length)];

    public static Member createMember() {
        return createMember(defaultId, defaultAccessLevel);
    }

    public static Member createMember(int id, AccessLevel accessLevel) {
        Member member = new Member();

        member.setId(id);
        member.setAccessLevel(accessLevel);

        return member;
    }
}
