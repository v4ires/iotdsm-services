package utils.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public interface MongoOperation {

    /**
     * Método que cria uma nova Base de Dados no MongoDB
     *
     * @return boolean
     */
    public boolean createMongoDB();

    /**
     * Método que retorna uma referência para o Banco de Dados do MongoDB
     *
     * @param db_name
     * @return
     */
    public MongoDatabase getMongoDatabase(String db_name);

    /**
     * Método que cria uma coleção em uma Base de Dados no MongoDB
     *
     * @param db
     * @return boolean
     */
    public boolean createMongoCollection(MongoDatabase db);

    /**
     * Método que retorna uma Collection do MongoDB
     *
     * @param db
     * @param collection
     * @return
     */
    public MongoCollection<Document> getMongoCollection(MongoDatabase db, String db_name, String collection);

    /**
     * Método que retorna a quantidade de itens em uma collection
     *
     * @param collection
     * @return long
     */
    public long getCollectionCount(DBCollection collection);

    /**
     * Método que cria um novo documento do MongoDB
     *
     * @return
     */
    public Document createMongoDocument(String json);

    /**
     * Método que insere um documento no MongoDB
     *
     * @param document
     * @return
     */
    public boolean insert_mongo(DBObject document, DBCollection collection);

    /**
     * Método que atualiza um documento no MongoDB
     *
     * @param document
     * @return
     */
    public boolean update_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Método que deleta um Documento de uma Colletion do MongoDB
     *
     * @param document
     * @param collection
     * @return
     */
    public boolean delete_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Método que insere uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Método que atualiza uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Método que remove uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean remove_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Método para fechar conexões ativas
     *
     * @return
     */
    public void close();
}
