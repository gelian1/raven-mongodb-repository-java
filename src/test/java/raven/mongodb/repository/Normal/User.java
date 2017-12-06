package raven.mongodb.repository.Normal;

import org.bson.codecs.pojo.annotations.BsonId;
import raven.data.entity.*;
import raven.data.entity.annotations.*;

@BsonPropertyFormat(BsonPropertyFormatType.PascalCase)
public final class User implements AutoIncr<Long> {

    @BsonId()
    private long id;

    private String name;

    private int age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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