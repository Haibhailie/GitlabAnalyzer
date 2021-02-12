package ca.sfu.orcus.gitlabanalyzer.authentication;

public class User {
    public String username;
    public String password;
    public String authToken;
    public String jwt;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private User(String authToken) {
        this.authToken = authToken;
    }

    public static User fromUsernameAndPassword(String username, String password) {
        return new User(username, password);
    }

    public static User fromAuthToken(String authToken) {
        return new User(authToken);
    }
}
