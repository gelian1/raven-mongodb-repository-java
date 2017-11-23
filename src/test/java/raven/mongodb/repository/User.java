package raven.mongodb.repository;

import org.bson.codecs.pojo.annotations.BsonId;
import raven.data.entity.*;


public final class User implements EntityLongKey, AutoIncr {

    @BsonId()
    private long id;

    private String name;

    private int age;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User() {
        id = 0L;
    }

}