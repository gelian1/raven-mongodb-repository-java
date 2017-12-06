package raven.mongodb.repository.StringID;

import com.mongodb.WriteConcern;
import raven.mongodb.repository.MongoRepositoryImpl;

public class User_StringIDRepository extends MongoRepositoryImpl<User_StringID,String> {
    public User_StringIDRepository(){
        super("mongodb://218.244.136.30:27018/"
                , "RepositoryTest2", null, WriteConcern.ACKNOWLEDGED, null, null);
    }
}
