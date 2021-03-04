package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.User;

import java.util.Random;
import java.util.UUID;

public class UserMock {
    private static final Random rand = new Random();
    private static final int upperBound = 1000;

    // TODO: Check if we need name, username, email, etc for other packages
    public static final int defaultId = rand.nextInt(upperBound);
    public static final String defaultName = UUID.randomUUID().toString();
    public static final String defaultUsername = UUID.randomUUID().toString();
    public static final String defaultEmail = UUID.randomUUID().toString();

    public static User createUser() {
        return createUser(defaultId, defaultName, defaultUsername, defaultEmail);
    }

    public static User createUser(int id, String name, String username, String email) {
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);

        return user;
    }
}
