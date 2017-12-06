package raven.mongodb.repository.ObjectID;

import com.mongodb.WriteConcern;
import org.bson.types.ObjectId;
import raven.data.entity.EntityObjectKey;
import raven.data.entity.EntityStringKey;
import raven.mongodb.repository.MongoRepositoryImpl;

public class User_ObjectIDRepository extends MongoRepositoryImpl<User_ObjectID,ObjectId> {
    public User_ObjectIDRepository(){
        super("mongodb://218.244.136.30:27018/"
                , "RepositoryTest2", null, null, null, null);
    }
}
