package raven.mongodb.repository.Normal;

import com.mongodb.WriteConcern;
import raven.mongodb.repository.MongoRepositoryImpl;

public class UserRepositoryImpl extends MongoRepositoryImpl<User, Long> {
    public UserRepositoryImpl() {
        super("mongodb://218.244.136.30:27018/"
                , "RepositoryTest2", null, WriteConcern.ACKNOWLEDGED, null, null);

    }
}
