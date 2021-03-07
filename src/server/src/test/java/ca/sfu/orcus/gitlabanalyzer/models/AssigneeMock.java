package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Assignee;

public class AssigneeMock {

    public static final String defaultAssigneeName = "John";

    public static Assignee createAssignee(int userId) {
        Assignee assignee = new Assignee();
        assignee.setName(defaultAssigneeName);
        assignee.setId(userId);
        return assignee;
    }
}
