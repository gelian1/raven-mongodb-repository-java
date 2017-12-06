package raven.data.entity;

import org.bson.types.ObjectId;


public interface EntityObjectKey extends Entity {
    ObjectId getId();
    void setId(ObjectId id);
}
