package ca.sfu.orcus.gitlabanalyzer.authentication;

public class AuthenticationUser {
    private String username;
    private String password;
    private String pat;
    private String authToken;
    private String jwt;

    public AuthenticationUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationUser(String pat) {
        this.pat = pat;
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
