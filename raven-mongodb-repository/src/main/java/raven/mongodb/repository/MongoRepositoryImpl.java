package raven.mongodb.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import raven.data.entity.*;
import raven.mongodb.repository.exceptions.FailedException;

import java.util.List;

public class MongoRepositoryImpl<TEntity extends Entity<TKey>, TKey>
        extends MongoReaderRepositoryImpl<TEntity, TKey>
        implements MongoRepository<TEntity, TKey> {

    //#region 构造函数

    /**
     * 构造函数
     *
     * @param connString     数据库连接节点
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     * @param writeConcern   WriteConcern
     * @param readPreference ReadPreference
     * @param sequence       Mongo自增长ID数据序列对象
     */
    public MongoRepositoryImpl(final String connString, final String dbName, final String collectionName, final WriteConcern writeConcern, final ReadPreference readPreference, final MongoSequence sequence) {
        super(connString, dbName, collectionName, writeConcern, readPreference, sequence);
    }

    /**
     * 构造函数
     *
     * @param connString 数据库连接节点
     * @param dbName     数据库名称
     */
    public MongoRepositoryImpl(final String connString, final String dbName) {
        super(connString, dbName);
    }

    /**
     * 构造函数
     *
     * @param options
     * @see MongoRepositoryOptions
     */
    public MongoRepositoryImpl(final MongoRepositoryOptions options) {
        super(options);
    }

    //#endregion

    //#region insert

    /**
     * @param entity
     * @throws FailedException
     */
    @Override
    public void insert(final TEntity entity)
            throws FailedException {
        this.insert(entity, null);
    }

    /**
     * @param entity
     * @param writeConcern
     * @throws FailedException
     */
    @Override
    public void insert(final TEntity entity, final WriteConcern writeConcern)
            throws FailedException {
        if (isAutoIncrClass) {
            super.createIncID(entity);
        } else if (keyClazz.equals(Util.OBJECT_ID_CLASS) && ((Entity<ObjectId>) entity).getId() == null) {
            super.createObjectID(entity);
        }
        super.getCollection(writeConcern).insertOne(entity);
    }

    /**
     * @param entitys
     * @throws FailedException
     */
    @Override
    public void insertBatch(final List<TEntity> entitys)
            throws FailedException {
        this.insertBatch(entitys, null);
    }

    /**
     * @param entitys
     * @param writeConcern
     * @throws FailedException
     */
    @Override
    public void insertBatch(final List<TEntity> entitys, final WriteConcern writeConcern)
            throws FailedException {
        //需要自增的实体
        if (isAutoIncrClass) {
            int count = entitys.size();
            //自增ID值
            long id = createIncID(count);
            id = id - count;

            for (TEntity entity : entitys) {
                assignmentEntityID(entity, ++id);
            }
        } else if (keyClazz.equals(Util.OBJECT_ID_CLASS)) {
            for (TEntity entity : entitys) {
                if (((Entity<ObjectId>) entity).getId() == null) {
                    super.createObjectID(entity);
                }
            }
        }

        super.getCollection(writeConcern).insertMany(entitys);
    }

    //#endregion

    /**
     * @param updateEntity
     * @param isUpsert
     * @return
     * @throws FailedException
     */
    protected Bson createUpdateBson(final TEntity updateEntity, final Boolean isUpsert)
            throws FailedException {
        long id = 0;
        BsonDocument bsDoc = super.toBsonDocument(updateEntity);
        bsDoc.remove(Util.PRIMARY_KEY_NAME);

        Bson update = new BsonDocument("$set", bsDoc);
        if (isUpsert && isAutoIncrClass) {
            id = createIncID();
            update = Updates.combine(update, Updates.setOnInsert(Util.PRIMARY_KEY_NAME, id));
        }

        return update;

    }

    //#region update

    /**
     * 修改单条数据
     *
     * @param filter
     * @param updateEntity
     * @return
     */
    @Override
    public UpdateResult updateOne(final Bson filter, final TEntity updateEntity)
            throws FailedException {
        return this.updateOne(filter, updateEntity, false, null);
    }

    /**
     * 修改单条数据
     *
     * @param filter
     * @param updateEntity
     * @param isUpsert
     * @param writeConcern
     * @return
     */
    @Override
    public UpdateResult updateOne(final Bson filter, final TEntity updateEntity, final Boolean isUpsert, final WriteConcern writeConcern)
            throws FailedException {

        UpdateOptions options = new UpdateOptions();
        options.upsert(isUpsert);

        Bson update = createUpdateBson(updateEntity, isUpsert);

        return super.getCollection(writeConcern).updateOne(filter, update, options);
    }

    /**
     * 修改单条数据
     *
     * @param filter
     * @param update
     * @return
     */
    @Override
    public UpdateResult updateOne(final Bson filter, final Bson update) {

        UpdateOptions options = new UpdateOptions();
        return super.getCollection().updateOne(filter, update, options);
    }

    /**
     * 修改单条数据
     *
     * @param filter
     * @param update
     * @param isUpsert
     * @param writeConcern
     * @return
     */
    @Override
    public UpdateResult updateOne(final Bson filter, final Bson update, final Boolean isUpsert, final WriteConcern writeConcern) {

        UpdateOptions options = new UpdateOptions();
        options.upsert(isUpsert);
        return super.getCollection(writeConcern).updateOne(filter, update, options);
    }

    /**
     * 修改多条数据
     *
     * @param filter
     * @param update
     * @return
     */
    @Override
    public UpdateResult updateMany(final Bson filter, final Bson update) {
        return super.getCollection().updateMany(filter, update);
    }

    /**
     * 修改多条数据
     *
     * @param filter
     * @param update
     * @param writeConcern
     * @return
     */
    @Override
    public UpdateResult updateMany(final Bson filter, final Bson update, final WriteConcern writeConcern) {
        return super.getCollection(writeConcern).updateMany(filter, update);
    }

    //#endregion

    //#region findAndModify

    /**
     * 找到并更新
     *
     * @param filter
     * @param update
     * @return
     */
    @Override
    public TEntity findOneAndUpdate(final Bson filter, final Bson update) {
        return this.findOneAndUpdate(filter, update, false, null);
    }

    /**
     * 找到并更新
     *
     * @param filter
     * @param update
     * @param isUpsert default false
     * @param sort
     * @return
     */
    @Override
    public TEntity findOneAndUpdate(final Bson filter, final Bson update, final Boolean isUpsert, final Bson sort) {

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(isUpsert);
        if (sort != null) {
            options.sort(sort);
        }

        return super.getCollection().findOneAndUpdate(filter, update, options);
    }

    /**
     * 找到并更新
     *
     * @param filter
     * @param entity
     * @return
     * @throws FailedException
     */
    @Override
    public TEntity findOneAndUpdate(final Bson filter, final TEntity entity)
            throws FailedException {
        return this.findOneAndUpdate(filter, entity, false, null);
    }

    /**
     * 找到并更新
     *
     * @param filter
     * @param entity
     * @param isUpsert default false
     * @param sort
     * @return
     * @throws FailedException
     */
    @Override
    public TEntity findOneAndUpdate(final Bson filter, final TEntity entity, final Boolean isUpsert, final Bson sort)
            throws FailedException {

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(isUpsert);
        if (sort != null) {
            options.sort(sort);
        }

        Bson update = createUpdateBson(entity, isUpsert);

        return super.getCollection().findOneAndUpdate(filter, update, options);
    }

    /**
     * 找到并删除
     *
     * @param filter
     * @return
     */
    @Override
    public TEntity findOneAndDelete(final Bson filter) {
        return super.getCollection().findOneAndDelete(filter);
    }

    /**
     * 找到并删除
     *
     * @param filter
     * @param sort
     * @return
     */
    @Override
    public TEntity findOneAndDelete(final Bson filter, final Bson sort) {

        FindOneAndDeleteOptions option = new FindOneAndDeleteOptions();
        if (sort != null) {
            option.sort(sort);
        }

        return super.getCollection().findOneAndDelete(filter, option);
    }

    //#endregion

    //#region delete

    /**
     * @param id 主键
     * @return
     */
    @Override
    public DeleteResult deleteOne(final TKey id) {
        Bson filter = Filters.eq(Util.PRIMARY_KEY_NAME, id);
        return super.getCollection().deleteOne(filter);
    }

    /**
     * @param id           主键
     * @param writeConcern WriteConcern
     * @return
     */
    @Override
    public DeleteResult deleteOne(final TKey id, final WriteConcern writeConcern) {
        Bson filter = Filters.eq(Util.PRIMARY_KEY_NAME, id);
        return super.getCollection(writeConcern).deleteOne(filter);
    }

    /**
     * @param filter
     * @return
     */
    @Override
    public DeleteResult deleteOne(final Bson filter) {
        return super.getCollection().deleteOne(filter);
    }

    /**
     * @param filter
     * @param writeConcern WriteConcern
     * @return
     */
    @Override
    public DeleteResult deleteOne(final Bson filter, final WriteConcern writeConcern) {
        return super.getCollection(writeConcern).deleteOne(filter);
    }

    /**
     * @param filter
     * @return
     */
    @Override
    public DeleteResult deleteMany(final Bson filter) {
        return super.getCollection().deleteMany(filter);
    }

    /**
     * @param filter
     * @param writeConcern WriteConcern
     * @return
     */
    @Override
    public DeleteResult deleteMany(final Bson filter, final WriteConcern writeConcern) {
        return super.getCollection(writeConcern).deleteMany(filter);
    }


    //#endregion


}
