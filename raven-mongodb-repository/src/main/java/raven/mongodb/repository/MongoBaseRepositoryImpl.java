package raven.mongodb.repository;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import raven.data.entity.*;
import raven.mongodb.repository.conventions.CustomConventions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @param <TEntity>
 * @param <TKey>
 */
public abstract class MongoBaseRepositoryImpl<TEntity, TKey>
        implements MongoBaseRepository<TEntity> {
    protected Class<TEntity> entityClazz;
    protected Class<TKey> keyClazz;

    /**
     * Mongo自增长ID数据序列
     */
    protected MongoSequence _sequence;

    protected MongoSession _mongoSession;

    private String collectionName;

    private CodecRegistry pojoCodecRegistry;

    /**
     * 集合名称
     *
     * @return
     */
    protected String getCollectionName() {
        return collectionName;
    }

    /**
     * @return
     */
    @Override
    public MongoDatabase getDatabase() {

        return _mongoSession.getDatabase().withCodecRegistry(pojoCodecRegistry);

    }

    //#region 构造函数

    private MongoBaseRepositoryImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClazz = (Class) params[0];
        keyClazz = (Class) params[1];

        pojoCodecRegistry = MongoClient.getDefaultCodecRegistry();

        ClassModel<TEntity> classModel = ClassModel.builder(entityClazz).conventions(CustomConventions.DEFAULT_CONVENTIONS).build();
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(classModel).build();
        pojoCodecRegistry = fromRegistries(pojoCodecRegistry, fromProviders(pojoCodecProvider));
    }

    /**
     * 构造函数
     *
     * @param connString     数据库连接节点
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     * @param writeConcern   WriteConcern
     * @param readPreference ReadPreference
     * @param sequence       Mongo自增长ID数据序列对象
     * @see WriteConcern
     * @see ReadPreference
     */
    public MongoBaseRepositoryImpl(final String connString, final String dbName, final String collectionName, final WriteConcern writeConcern, final ReadPreference readPreference, final MongoSequence sequence) {
        this();
        this._sequence = sequence != null ? sequence : new MongoSequence();
        this._mongoSession = new MongoSession(connString, dbName, writeConcern, false, readPreference);
        this.collectionName = collectionName;
        if (this.collectionName == null || this.collectionName.isEmpty()) {
            //String[] arr = entityClazz.getName().split("\\.");
            this.collectionName = entityClazz.getSimpleName();
        }
    }

    /**
     * 构造函数
     *
     * @param connString 数据库连接节点
     * @param dbName     数据库名称
     */
    public MongoBaseRepositoryImpl(final String connString, final String dbName) {
        this(connString, dbName, null, null, null, null);
    }

    /**
     * 构造函数
     *
     * @param options
     * @see MongoRepositoryOptions
     */
    public MongoBaseRepositoryImpl(final MongoRepositoryOptions options) {
        this(options.connString, options.dbName, options.collectionName, options.writeConcern, options.readPreference, options.sequence);
    }

    //#endregion


    //#region getCollection

    /**
     * 根据数据类型得到集合
     *
     * @return
     */
    @Override
    public MongoCollection<TEntity> getCollection() {
        return getDatabase().getCollection(getCollectionName(), entityClazz);
    }

    /**
     * 根据数据类型得到集合
     *
     * @param writeConcern WriteConcern
     * @return
     * @see WriteConcern
     */
    @Override
    public MongoCollection<TEntity> getCollection(WriteConcern writeConcern) {
        MongoCollection<TEntity> collection = this.getCollection();
        if (writeConcern != null) {
            collection = collection.withWriteConcern(writeConcern);
        }
        return collection;
    }

    /**
     * 根据数据类型得到集合
     *
     * @param readPreference ReadPreference
     * @return
     * @see ReadPreference
     */
    @Override
    public MongoCollection<TEntity> getCollection(ReadPreference readPreference) {
        MongoCollection<TEntity> collection = this.getCollection();
        if (readPreference != null) {
            collection = collection.withReadPreference(readPreference);
        }
        return collection;
    }

    //#endregion

    /**
     * @param entity
     * @return
     */
    protected BsonDocument toBsonDocument(TEntity entity) {

        return new BsonDocumentWrapper<TEntity>(entity, pojoCodecRegistry.get(entityClazz));
    }

    /**
     * @param includeFields
     * @return
     */
    public Bson IncludeFields(List<String> includeFields) {

        Bson projection = null;
        if (includeFields != null && includeFields.size() > 0) {
            projection = Projections.include(includeFields);
        }

        return projection;
    }


//    public Bson CreateSortBson(List<String> fieldNames, SortType sortType) {
//
//        Bson sort = null;
//        if (sortType == SortType.Ascending) {
//            sort = Sorts.ascending(fieldNames);
//        }
//        else{
//            sort = Sorts.descending(fieldNames);
//        }
//
//        return sort;
//    }


    /**
     * @param findIterable
     * @param projection
     * @param sort
     * @param limit
     * @param skip
     * @param hint
     * @return
     */
    public FindIterable<TEntity> findOptions(FindIterable<TEntity> findIterable, Bson projection, Bson sort
            , int limit, int skip, BsonValue hint) {

        if (limit > 0) {
            findIterable = findIterable.projection(projection);
        }

        if (limit > 0) {
            findIterable = findIterable.limit(limit);
        }

        if (skip > 0) {
            findIterable = findIterable.skip(skip);
        }

        if (sort != null) {
            findIterable = findIterable.sort(sort);
        }

        if (hint != null) {
            Bson hintBson = new BsonDocument("$hint", hint);
            findIterable = findIterable.hint(hintBson);
        }

        return findIterable;

    }


    /// <summary>
    /// ID赋值
    /// </summary>
    /// <param name="entity"></param>
    /// <param name="id"></param>
    protected void assignmentEntityID(TEntity entity, long id) {
        Entity<TKey> tEntity = (Entity<TKey>) entity;

//        if (entity instanceof EntityIntKey) {
//            ((EntityIntKey) entity).setId((int) id);
//        } else if (entity instanceof EntityLongKey) {
//            ((EntityLongKey) entity).setId(id);
//        }

        if (keyClazz.equals(Integer.class)) {
            ((Entity<Integer>) tEntity).setId((int) id);
        } else if (keyClazz.equals(Long.class)) {
            ((Entity<Long>) tEntity).setId(id);
        } else if (keyClazz.equals(Short.class)) {
            ((Entity<Short>) tEntity).setId((short) id);
        }

    }

}
