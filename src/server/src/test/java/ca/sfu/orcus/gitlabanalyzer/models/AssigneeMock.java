package ca.sfu.orcus.gitlabanalyzer.models;

import org.gitlab4j.api.models.Assignee;

import static ca.sfu.orcus.gitlabanalyzer.models.MergeRequestMock.userIdB;

public class AssigneeMock {

    public static final String assigneeName = "John";

    public static Assignee generateAssignee(int userId){
        Assignee assignee = new Assignee();
        assignee.setName(assigneeName);
        assignee.setId(userIdB);
        return assignee;
    }
}
