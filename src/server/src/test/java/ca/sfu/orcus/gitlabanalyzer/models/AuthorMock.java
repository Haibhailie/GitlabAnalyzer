package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Author;

public class AuthorMock {

    public static final String defaultAuthorName = "John";
    public static final int defaultUserId = 6;
    public static Author generateAuthor() {
        Author tempAuthor = new Author();
        tempAuthor.setName(defaultAuthorName);
        tempAuthor.setId(defaultUserId);
        return tempAuthor;
    }
}
