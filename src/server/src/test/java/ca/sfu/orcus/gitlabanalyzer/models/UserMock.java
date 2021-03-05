package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.User;

import java.util.Random;

public final class UserMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;
    
    public static final int defaultId = rand.nextInt(upperBound);

    public static User createUser() {
        return createUser(defaultId);
    }

    public static User createUser(int id) {
        User user = new User();

        user.setId(id);

        return user;
    }
}
