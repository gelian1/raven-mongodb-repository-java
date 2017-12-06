package raven.mongodb.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import raven.mongodb.repository.exceptions.FailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <TEntity>
 * @param <TKey>
 */
public class MongoReaderRepositoryImpl<TEntity, TKey>
        extends MongoBaseRepositoryImpl<TEntity, TKey>
        implements MongoReaderRepository<TEntity, TKey> {

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
    public MongoReaderRepositoryImpl(final String connString, final String dbName, final String collectionName, final WriteConcern writeConcern, final ReadPreference readPreference, final MongoSequence sequence) {
        super(connString, dbName, collectionName, writeConcern, readPreference, sequence);
    }

    /**
     * 构造函数
     *
     * @param connString 数据库连接节点
     * @param dbName     数据库名称
     */
    public MongoReaderRepositoryImpl(final String connString, final String dbName) {
        super(connString, dbName);
    }

    /**
     * 构造函数
     *
     * @param options
     * @see MongoRepositoryOptions
     */
    public MongoReaderRepositoryImpl(final MongoRepositoryOptions options) {
        super(options);
    }

    //#endregion

    /**
     * @return
     * @throws FailedException
     */
    @Override
    public long createIncID() throws FailedException {
        return createIncID(1);
    }

    /**
     * @param inc
     * @return
     * @throws FailedException
     */
    @Override
    public long createIncID(long inc) throws FailedException {
        return createIncID(inc, 0);
    }

    /**
     * @param inc
     * @param iteration
     * @return
     * @throws FailedException
     */
    @Override
    public long createIncID(long inc, int iteration) throws FailedException {
        long id = 1;
        MongoCollection<BsonDocument> collection = getDatabase().getCollection(super._sequence.getSequenceName(), BsonDocument.class);
        String typeName = getCollectionName();

        Bson filter = Filters.eq(super._sequence.getCollectionName(), typeName);
        Bson updater = Updates.inc(super._sequence.getIncrementID(), inc);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options = options.upsert(true).returnDocument(ReturnDocument.AFTER);

        BsonDocument result = collection.findOneAndUpdate(filter, updater, options);
        if (result != null) {
            id = result.getInt64(super._sequence.getIncrementID()).longValue();
            //id = result[super._sequence.getIncrementID()].AsInt64;
            return id;
        } else if (iteration <= 1) {
            return createIncID(inc, ++iteration);
        } else {
            throw new FailedException("Failed to get on the IncID");
        }
    }

    //#region get

    /**
     * 创建自增ID
     *
     * @param entity
     * @throws FailedException
     */
    @Override
    public void createIncID(TEntity entity)
            throws FailedException {
        long _id = 0;
        _id = this.createIncID();
        assignmentEntityID(entity, _id);
    }


    /**
     * 根据id获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TEntity get(TKey id) {
        return this.get(id, null);
    }

    /**
     * 根据id获取实体
     *
     * @param id
     * @param includeFields 查询字段
     * @return
     */
    @Override
    public TEntity get(TKey id, List<String> includeFields) {
        return this.get(id, includeFields, null);
    }

    /**
     * 根据id获取实体
     *
     * @param id
     * @param includeFields 查询字段
     * @param sort          排序
     * @return
     */
    @Override
    public TEntity get(TKey id, List<String> includeFields, Bson sort) {
        return this.get(id, includeFields, sort, null, null);
    }

    /**
     * 根据id获取实体
     *
     * @param id
     * @param includeFields  查询字段
     * @param sort           排序
     * @param hint           hint索引
     * @param readPreference 访问设置
     * @return
     */
    @Override
    public TEntity get(TKey id, List<String> includeFields, Bson sort, BsonValue hint
            , ReadPreference readPreference) {

        Bson filter = Filters.eq(Util.PRIMARY_KEY_NAME, id);

        Bson projection = null;
        if (includeFields != null) {
            projection = super.IncludeFields(includeFields);
        }

        FindIterable<TEntity> result = super.getCollection(readPreference).find(filter, entityClazz);
        result = super.findOptions(result, projection, sort, 1, 0, hint);

        return result.first();
    }


    /**
     * 根据条件获取实体
     *
     * @param filter 查询条件
     * @return
     */
    @Override
    public TEntity get(Bson filter) {
        return this.get(filter, null);
    }

    /**
     * 根据条件获取实体
     *
     * @param filter        查询条件
     * @param includeFields 查询字段
     * @return
     */
    @Override
    public TEntity get(Bson filter, List<String> includeFields) {
        return this.get(filter, includeFields, null);
    }

    /**
     * 根据条件获取实体
     *
     * @param filter        查询条件
     * @param includeFields 查询字段
     * @param sort          排序
     * @return
     */
    @Override
    public TEntity get(Bson filter, List<String> includeFields, Bson sort) {
        return this.get(filter, includeFields, sort, null, null);

    }

    /**
     * 根据条件获取实体
     *
     * @param filter         查询条件
     * @param includeFields  查询字段
     * @param sort           排序
     * @param hint           hint索引
     * @param readPreference 访问设置
     * @return
     */
    @Override
    public TEntity get(Bson filter, List<String> includeFields, Bson sort, BsonValue hint
            , ReadPreference readPreference) {

        if (filter == null) {
            filter = new BsonDocument();
        }

        Bson projection = null;
        if (includeFields != null) {
            projection = super.IncludeFields(includeFields);
        }

        FindIterable<TEntity> result = super.getCollection(readPreference).find(filter, entityClazz);
        result = super.findOptions(result, projection, sort, 1, 0, hint);

        return result.first();
    }

    //#endregion

    //#region getList

    /**
     * 根据条件获取获取列表
     *
     * @param filter 查询条件
     * @return
     */
    @Override
    public ArrayList<TEntity> getList(Bson filter) {
        return this.getList(filter, null);
    }

    /**
     * 根据条件获取获取列表
     *
     * @param filter        查询条件
     * @param includeFields 查询字段
     * @return
     */
    @Override
    public ArrayList<TEntity> getList(Bson filter, List<String> includeFields) {
        return this.getList(filter, includeFields, null);
    }

    /**
     * 根据条件获取获取列表
     *
     * @param filter        查询条件
     * @param includeFields 查询字段
     * @param sort          排序
     * @return
     */
    @Override
    public ArrayList<TEntity> getList(Bson filter, List<String> includeFields, Bson sort) {
        return this.getList(filter, includeFields, sort, 0, 0);
    }

    /**
     * 根据条件获取获取列表
     *
     * @param filter        查询条件
     * @param includeFields 查询字段
     * @param sort          排序
     * @param limit
     * @param skip
     * @return
     */
    @Override
    public ArrayList<TEntity> getList(Bson filter, List<String> includeFields, Bson sort
            , int limit, int skip) {
        return this.getList(filter, includeFields, sort, limit, skip, null, null);
    }

    /**
     * 根据条件获取获取列表
     *
     * @param filter         查询条件
     * @param includeFields  查询字段
     * @param sort           排序
     * @param limit
     * @param skip
     * @param hint           hint索引
     * @param readPreference 访问设置
     * @return
     */
    @Override
    public ArrayList<TEntity> getList(Bson filter, List<String> includeFields, Bson sort
            , int limit, int skip
            , BsonValue hint
            , ReadPreference readPreference) {

        if (filter == null) {
            filter = new BsonDocument();
        }

        Bson projection = null;
        if (includeFields != null) {
            projection = super.IncludeFields(includeFields);
        }

        FindIterable<TEntity> result = super.getCollection(readPreference).find(filter, entityClazz);
        result = super.findOptions(result, projection, sort, limit, skip, hint);

        ArrayList<TEntity> list = new ArrayList<>();
        for (TEntity entity : result) {
            list.add(entity);
        }

        return list;
    }

    //#endregion

    /**
     * 数量
     *
     * @param filter 查询条件
     * @return
     */
    @Override
    public long count(Bson filter) {
        return this.count(filter, 0, 0, null, null);
    }

    /**
     * 数量
     *
     * @param filter         查询条件
     * @param limit
     * @param skip
     * @param hint           hint索引
     * @param readPreference 访问设置
     * @return
     */
    @Override
    public long count(Bson filter, int limit, int skip, BsonValue hint
            , ReadPreference readPreference) {

        if (filter == null) {
            filter = new BsonDocument();
        }

        CountOptions option = new CountOptions();
        option.limit(limit);
        option.limit(skip);

        return super.getCollection(readPreference).count(filter, option);
    }

    /**
     * 是否存在
     *
     * @param filter
     * @return
     */
    @Override
    public boolean exists(Bson filter) {
        return exists(filter, null, null);
    }

    /**
     * 是否存在
     *
     * @param filter
     * @param hint
     * @param readPreference
     * @return
     */
    @Override
    public boolean exists(Bson filter, BsonValue hint
            , ReadPreference readPreference) {

        if (filter == null) {
            filter = new BsonDocument();
        }

        List<String> includeFields = new ArrayList<>(1);
        includeFields.add(Util.PRIMARY_KEY_NAME);

        return this.get(filter, includeFields, null, hint, readPreference) != null;
    }

}
