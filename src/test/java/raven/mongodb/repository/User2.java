package raven.mongodb.repository;

import org.bson.codecs.pojo.annotations.BsonId;
import raven.data.entity.AutoIncr;
import raven.data.entity.EntityLongKey;

import java.time.LocalDateTime;

public class User2 implements EntityLongKey, AutoIncr {

    @BsonId()
    private long id;

    private String name;

    private LocalDateTime createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User2() {
        id = 0L;
    }

}
