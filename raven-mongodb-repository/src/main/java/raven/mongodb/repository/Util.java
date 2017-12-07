package raven.mongodb.repository;

import org.bson.types.ObjectId;
import raven.data.entity.AutoIncr;

class Util {
    public static final String PRIMARY_KEY_NAME = "_id";

    public static final Class<AutoIncr> AUTO_INCR_CLASS = AutoIncr.class;

    public static final Class<ObjectId> OBJECT_ID_CLASS = ObjectId.class;
}
