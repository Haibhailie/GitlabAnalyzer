package ca.sfu.orcus.gitlabanalyzer.member;

//[
//  {
//    "id": 1,
//    "username": "raymond_smith",
//    "name": "Raymond Smith",
//    "state": "active",
//    "avatar_url": "https://www.gravatar.com/avatar/c2525a7f58ae3776070e44c106c48e15?s=80&d=identicon",
//    "web_url": "http://192.168.1.8:3000/root",
//    "last_activity_on": "2021-01-27"
//  },
//  {
//    "id": 2,
//    "username": "john_doe",
//    "name": "John Doe",
//    "state": "active",
//    "avatar_url": "https://www.gravatar.com/avatar/c2525a7f58ae3776070e44c106c48e15?s=80&d=identicon",
//    "web_url": "http://192.168.1.8:3000/root",
//    "email": "john@example.com",
//    "last_activity_on": "2021-01-25"
//  },
//  {
//    "id": 3,
//    "username": "foo_bar",
//    "name": "Foo bar",
//    "state": "active",
//    "avatar_url": "https://www.gravatar.com/avatar/c2525a7f58ae3776070e44c106c48e15?s=80&d=identicon",
//    "web_url": "http://192.168.1.8:3000/root",
//    "last_activity_on": "2021-01-20"
//  }
//]

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
