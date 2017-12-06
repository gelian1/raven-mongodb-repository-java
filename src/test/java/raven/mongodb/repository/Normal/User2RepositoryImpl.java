package raven.mongodb.repository.Normal;

import com.mongodb.WriteConcern;
import raven.mongodb.repository.MongoRepositoryImpl;

public class User2RepositoryImpl extends MongoRepositoryImpl<User2, Long> {
    public User2RepositoryImpl() {
        super("mongodb://218.244.136.30:27018/"
                , "RepositoryTest2", null, WriteConcern.ACKNOWLEDGED, null, null);

    }
}