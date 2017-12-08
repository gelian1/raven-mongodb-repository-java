package raven.mongodb.repository.objectID;

import org.bson.codecs.pojo.annotations.*;

import org.bson.types.ObjectId;
import raven.data.entity.*;
import raven.data.entity.annotations.BsonPropertyFormat;
import raven.data.entity.annotations.BsonPropertyFormatType;

import java.util.Date;

/**
 * User实体-自生成的ObjectId
 */
@BsonPropertyFormat(BsonPropertyFormatType.PascalCase)
public class User_ObjectID implements Entity<ObjectId> {

    //主键
    @BsonId
    private ObjectId _id;

    //姓名
    private String name;

    /*private String Address;

    public String getAddress() {
        return Address;
    }
    public void setAddress(String address){this.Address = address;}*/

    //年龄
    private int age;

    //创建时间
    private Date createTime;

    //#region 主键
    public ObjectId getId(){return _id;}

    public void setId(ObjectId _id){this._id = _id;}
    //#endregion

    //#region Name
    public String getName(){return name;}

    public void setName(String name){this.name = name;}
    //#endregion

    //#region CreateTime
    public Date getCreateTime(){return createTime;}

    public void setCreateTime(Date createTime){this.createTime = createTime;}
    //#endregion


    public  User_ObjectID(){
        this.createTime = new Date();
        //this.id = new ObjectId();
        //this.Address = "add哈哈";
    }
}
