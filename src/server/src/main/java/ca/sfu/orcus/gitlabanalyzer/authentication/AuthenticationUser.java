package ca.sfu.orcus.gitlabanalyzer.authentication;

public class AuthenticationUser {
    private String username;
    private String password;
    private String pat;
    private String jwt;

    private AuthenticationUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private AuthenticationUser(String pat) {
        this.pat = pat;
    }

    public static AuthenticationUser fromUsernameAndPassword(String username, String password) {
        return new AuthenticationUser(username, password);
    }

    // Used by Spring Boot automatically to parse request body
    public static AuthenticationUser createFromAuthToken(String authToken) {
        return new AuthenticationUser(authToken);
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

    public void setUsername(String username) {
        this.username = username;
    }
}
