package raven.mongodb.repository;

import com.mongodb.*;
import com.mongodb.client.*;


public class MongoSession {

    /// <summary>
    /// MongoDB WriteConcern
    /// </summary>
    private WriteConcern _writeConcern;

    public WriteConcern get_writeConcern() {
        return _writeConcern;
    }

    /// <summary>
    /// MongoDB ReadPreference
    /// </summary>
    private ReadPreference _readPreference;

    public ReadPreference get_readPreference() {
        return _readPreference;
    }

    /// <summary>
    /// MongoClient
    /// </summary>
    private MongoClient _mongoClient;

    /**
     * @return MongoDatabase
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    private MongoDatabase database;

    /**
     * 构造函数
     *
     * @param connString     数据库链接字符串
     * @param dbName         数据库名称
     * @param writeConcern   WriteConcern选项
     * @param isSlaveOK
     * @param readPreference
     */
    public MongoSession(final String connString, final String dbName, final WriteConcern writeConcern, final Boolean isSlaveOK, final ReadPreference readPreference) {
        //this(new MongoClient(connString), dbName, writeConcern, isSlaveOK, readPreference);
        this._writeConcern = writeConcern != null ? writeConcern : WriteConcern.UNACKNOWLEDGED;
        this._readPreference = readPreference != null ? readPreference : ReadPreference.secondaryPreferred();

        MongoClientURI mongoClientURI = new MongoClientURI(connString);
        _mongoClient = new MongoClient(mongoClientURI);

        database = _mongoClient.getDatabase(dbName).withReadPreference(this._readPreference).withWriteConcern(this._writeConcern);
    }


}
