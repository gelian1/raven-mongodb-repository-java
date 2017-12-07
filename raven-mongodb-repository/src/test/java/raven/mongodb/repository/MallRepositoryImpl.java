package raven.mongodb.repository;

import com.mongodb.WriteConcern;
import raven.mongodb.repository.entitys.Mall;

public class MallRepositoryImpl extends MongoRepositoryImpl<Mall, String> {
    public MallRepositoryImpl() {
        super("mongodb://127.0.0.1:27017/"
                , "RepositoryTest2", null, WriteConcern.ACKNOWLEDGED, null, null);

    }
}
