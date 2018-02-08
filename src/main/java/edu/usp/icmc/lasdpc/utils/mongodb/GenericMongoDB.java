package edu.usp.icmc.lasdpc.utils.mongodb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class GenericMongoDB implements MongoOperation {

    private final MongoClient mongoFactory;
    private Gson gson;

    /**
     * Construtor da Classe GenericMongoDB
     *
     * @param mongoFactory
     */
    public GenericMongoDB(MongoClient mongoFactory) {
        this.mongoFactory = mongoFactory;
        gson = new Gson();

        MongoDBUtil.createIndexIfNotExists(this, "sensor", "id_index", "id", true);
        MongoDBUtil.createIndexIfNotExists(this, "sensor_measure", "id_index", "id", true);
        MongoDBUtil.createIndexIfNotExists(this, "sensor_measure_type", "id_index", "id", true);
        MongoDBUtil.createIndexIfNotExists(this, "sensor_measure", "sensor_id_index", "sensor_id", false);
    }

    @Override
    public boolean createMongoDB() {
        return false;
    }

    @Override
    public MongoDatabase getMongoDatabase(String db_name) {
        return mongoFactory.getDatabase(db_name);
    }

    @Override
    public boolean createMongoCollection(MongoDatabase db) {
        return false;
    }

    @Override
    public MongoCollection<Document> getMongoCollection(String db_name, String collection) {
        return getMongoDatabase(db_name).getCollection(collection);
    }

    @Override
    public long getCollectionCount(DBCollection collection) {
        return collection.count();
    }

    @Override
    public Document createMongoDocument(String json) {
        Map<String, Object> map = gson
                .fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
        return new Document(map);
    }

    @Override
    public boolean insert_mongo(DBObject document, DBCollection collection) {
        return collection.insert(document) != null ? true : false;
    }

    @Override
    public boolean update_mongo(Document document, MongoCollection<Document> collection) {
        return false;
    }

    @Override
    public boolean delete_mongo(Document document, MongoCollection<Document> collection) {
        return false;
    }

    @Override
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        return false;
    }

    @Override
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        return false;
    }

    @Override
    public boolean remove_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        return false;
    }

    @Override
    public void close() {
        if (mongoFactory != null)
            mongoFactory.close();
    }
}
