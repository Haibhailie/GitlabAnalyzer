package ca.sfu.orcus.gitlabanalyzer.authentication;

public class User {
    private String username;
    private String password;
    private String pat;
    private String authToken;
    private String jwt;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private User(String pat) {
        this.pat = pat;
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

    public String getPat() {
        return pat;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getAuthToken() { return authToken; }

    public void setAuthToken(String authToken) { this.authToken = authToken; }
}
