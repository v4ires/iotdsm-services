package utils.mongodb;

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

    /**
     * Metodo que retonra a referencia de uma base de dados do Mongodb
     *
     * @param db_name
     * @return MongoDatabase
     */
    @Override
    public MongoDatabase getMongoDatabase(String db_name) {
        return mongoFactory.getDatabase(db_name);
    }

    @Override
    public boolean createMongoCollection(MongoDatabase db) {
        return false;
    }

    /**
     * Metodo que retorna uma collection especifica de uma base de dados do MongoDB
     *
     * @param db_name
     * @param collection
     * @return MongoCollection<Document>
     */
    @Override
    public MongoCollection<Document> getMongoCollection(String db_name, String collection) {
        return getMongoDatabase(db_name).getCollection(collection);
    }

    /**
     * Metodo que retorna a quantidade de itens em uma collection
     *
     * @param collection
     * @return long
     */
    @Override
    public long getCollectionCount(DBCollection collection) {
        return collection.count();
    }

    /**
     * Metodo que retorna um novo Documento para ser persistido no MongoDB
     *
     * @param json
     * @return Document
     */
    @Override
    public Document createMongoDocument(String json) {
        Map<String, Object> map = gson
                .fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
        return new Document(map);
    }

    /**
     * Metodo que insere um Documento no MongoDB
     *
     * @param document
     * @return boolean
     */
    @Override
    public boolean insert_mongo(DBObject document, DBCollection collection) {
        return collection.insert(document) != null ? true : false;
    }

    /**
     * Metodo que atualiza um Documento no MongoDB
     *
     * @param document
     * @param collection
     * @return boolean
     */
    @Override
    public boolean update_mongo(Document document, MongoCollection<Document> collection) {
        return false;
    }

    /**
     * Metodo que deleta um Documento no MongoDB
     *
     * @param document
     * @param collection
     * @return boolean
     */
    @Override
    public boolean delete_mongo(Document document, MongoCollection<Document> collection) {
        return false;
    }

    /**
     * Metodo que insere uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return boolean
     */
    @Override
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        return false;
    }

    /**
     * Metodo que atualiza uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return boolean
     */
    @Override
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        return false;
    }

    /**
     * Metodo que remove uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
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
