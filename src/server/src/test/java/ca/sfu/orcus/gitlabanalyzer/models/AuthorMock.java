package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Author;

public class AuthorMock {

    public static final String authorName = "John";
    public static final int userId = 6;
    public static Author generateAuthor(){
        Author tempAuthor = new Author();
        tempAuthor.setName(authorName);
        tempAuthor.setId(userId);
        return tempAuthor;
    }
}
