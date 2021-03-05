package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MemberMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;
    private static final AccessLevel[] accessLevels = AccessLevel.values();

    public static final String defaultDisplayName = UUID.randomUUID().toString();
    public static final String defaultEmail = UUID.randomUUID().toString();
    public static final int defaultId = rand.nextInt(upperBound);
    public static final String defaultUserName = UUID.randomUUID().toString();
    public static final AccessLevel defaultAccessLevel = accessLevels[rand.nextInt(accessLevels.length)];

    public static Member createMember() {
        return createMember(defaultDisplayName, defaultEmail, defaultId, defaultUserName, defaultAccessLevel);
    }

    public static Member createMember(String displayName, String email, int id, String userName, AccessLevel accessLevel) {
        Member member = new Member();

        member.setName(displayName);
        member.setEmail(email);
        member.setId(id);
        member.setUsername(userName);
        member.setAccessLevel(accessLevel);

        return member;
    }

    public static List<Member> createTestMemberList() {
        Member memberA = new Member();
        Member memberB = new Member();
        List<Member> members = new ArrayList<>();

        memberA.setName(defaultDisplayName);
        memberA.setEmail(defaultEmail);
        memberA.setId(defaultId);
        memberA.setUsername(defaultUserName);
        memberA.setAccessLevel(defaultAccessLevel);

        memberB.setName(defaultDisplayName);
        memberB.setEmail(defaultEmail);
        memberB.setId(defaultId);
        memberB.setUsername(defaultUserName);
        memberB.setAccessLevel(defaultAccessLevel);

        members.add(memberA);
        members.add(memberB);

        return members;
    }
}
