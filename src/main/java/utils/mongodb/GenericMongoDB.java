package utils.mongodb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.NotImplementedException;
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
        gson =  new Gson();
    }

    @Override
    public boolean createMongoDB() {
        return false;
    }

    /**
     * Método que retonra a referência de uma base de dados do Mongodb
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
     * Método que retorna uma collection especifica de uma base de dados do MongoDB
     *
     * @param db
     * @param db_name
     * @param collection
     * @return MongoCollection<Document>
     */
    @Override
    public MongoCollection<Document> getMongoCollection(MongoDatabase db, String db_name, String collection) {
        return getMongoDatabase(db_name).getCollection(collection);
    }

    /**
     * Método que retorna a quantidade de itens em uma collection
     *
     * @param collection
     * @return long
     */
    @Override
    public long getCollectionCount(DBCollection collection) {
        return collection.count();
    }

    /**
     * Método que retorna um novo Documento para ser persistido no MongoDB
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
     * Método que insere um Documento no MongoDB
     *
     * @param document
     * @return boolean
     */
    @Override
    public boolean insert_mongo(DBObject document, DBCollection collection) {
        return collection.insert(document) != null ? true : false;
    }

    /**
     * Método que atualiza um Documento no MongoDB
     *
     * @param document
     * @param collection
     * @return boolean
     */
    @Override
    public boolean update_mongo(Document document, MongoCollection<Document> collection) {
        throw new NotImplementedException();
    }

    /**
     * Método que deleta um Documento no MongoDB
     *
     * @param document
     * @param collection
     * @return boolean
     */
    @Override
    public boolean delete_mongo(Document document, MongoCollection<Document> collection) {
        throw new NotImplementedException();
    }

    /**
     * Método que insere uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return boolean
     */
    @Override
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        throw new NotImplementedException();
    }

    /**
     * Método que atualiza uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return boolean
     */
    @Override
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        throw new NotImplementedException();
    }

    /**
     * Método que remove uma lista de Documentos no MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    @Override
    public boolean remove_list_mongo(List<Document> documents, MongoCollection<Document> collection) {
        throw new NotImplementedException();
    }

    @Override
    public void close() {
        if(mongoFactory != null)
            mongoFactory.close();
    }
}
