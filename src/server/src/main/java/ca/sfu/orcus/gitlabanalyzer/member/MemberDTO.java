package ca.sfu.orcus.gitlabanalyzer.member;



public class MemberDTO {

    private String name;
    private String email;
    private Integer id;
    private String username;
    private String state;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setState(String state) {
        this.state = state;
    }


}
