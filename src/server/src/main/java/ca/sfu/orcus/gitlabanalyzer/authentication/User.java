package ca.sfu.orcus.gitlabanalyzer.authentication;

public class User {
    private String username;
    private String password;
    private String authToken;
    private String jwt;

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

    public static User createFromAuthToken(String authToken) {
        return new User(authToken);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
