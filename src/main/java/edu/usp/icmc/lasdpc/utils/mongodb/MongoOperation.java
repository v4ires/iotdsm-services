package edu.usp.icmc.lasdpc.utils.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public interface MongoOperation {

    /**
     * Metodo que cria uma nova Base de Dados no MongoDB
     *
     * @return boolean
     */
    public boolean createMongoDB();

    /**
     * Metodo que retorna uma referencia para o Banco de Dados do MongoDB
     *
     * @param db_name
     * @return
     */
    public MongoDatabase getMongoDatabase(String db_name);

    /**
     * Metodo que cria uma colecao em uma Base de Dados no MongoDB
     *
     * @param db
     * @return boolean
     */
    public boolean createMongoCollection(MongoDatabase db);

    /**
     * Metodo que retorna uma Collection do MongoDB
     *
     * @param db_name
     * @param collection
     * @return
     */
    public MongoCollection<Document> getMongoCollection(String db_name, String collection);

    /**
     * Metodo que retorna a quantidade de itens em uma collection
     *
     * @param collection
     * @return long
     */
    public long getCollectionCount(DBCollection collection);

    /**
     * Metodo que cria um novo documento do MongoDB
     *
     * @return
     */
    public Document createMongoDocument(String json);

    /**
     * Metodo que insere um documento no MongoDB
     *
     * @param document
     * @return
     */
    public boolean insert_mongo(DBObject document, DBCollection collection);

    /**
     * Metodo que atualiza um documento no MongoDB
     *
     * @param document
     * @return
     */
    public boolean update_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Metodo que deleta um Documento de uma Colletion do MongoDB
     *
     * @param document
     * @param collection
     * @return
     */
    public boolean delete_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Metodo que insere uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo que atualiza uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo que remove uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents
     * @param collection
     * @return
     */
    public boolean remove_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo para fechar conexoes ativas
     *
     * @return
     */
    public void close();
}
