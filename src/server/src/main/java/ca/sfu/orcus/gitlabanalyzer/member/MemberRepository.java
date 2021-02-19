package ca.sfu.orcus.gitlabanalyzer.member;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Repository
public class MemberRepository {
    MongoCollection<Document> collection;

    public MemberRepository(){

    }



}
