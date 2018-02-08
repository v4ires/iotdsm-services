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
     * @param db_name Nome do banco de dados
     * @return Retorna uma instância da classe MongoDatabase
     */
    public MongoDatabase getMongoDatabase(String db_name);

    /**
     * Metodo que cria uma colecao em uma Base de Dados no MongoDB
     *
     * @param db Instância do MongoDatabase
     * @return boolean Retorna uma instância da classe MongoDatabase
     */
    public boolean createMongoCollection(MongoDatabase db);

    /**
     * Metodo que retorna uma Collection do MongoDB
     *
     * @param db_name Nome do banco de dados
     * @param collection Coleção do MongoDB
     * @return Uma instância de uma coleção do MongoDB
     */
    public MongoCollection<Document> getMongoCollection(String db_name, String collection);

    /**
     * Metodo que retorna a quantidade de itens em uma collection
     *
     * @param collection Instância de uma coleção do MongoDB.
     * @return long Quantidade de informações de uma coleção.
     */
    public long getCollectionCount(DBCollection collection);

    /**
     * Metodo que cria um novo documento do MongoDB.
     *
     * @param json String no formato JSON
     * @return Documento do MongoDB.
     */
    public Document createMongoDocument(String json);

    /**
     * Metodo que insere um documento no MongoDB
     *
     * @param document Documento que representa um objeto do MongoDB.
     * @param  collection Coleção de uma base de dados do MongoDB.
     * @return Retorna se o documento foi inserido no MongoDB.
     */
    public boolean insert_mongo(DBObject document, DBCollection collection);

    /**
     * Metodo que atualiza um documento no MongoDB
     *
     * @param document Documento MongoDB que deseja atualizar.
     * @param collection Coleção onde se deseja atualizar o documento.
     * @return
     */
    public boolean update_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Metodo que deleta um Documento de uma Colletion do MongoDB
     *
     * @param document Documento MongoDB que deseja deletar.
     * @param collection Coleção onde se encontra o documento que se deseja deletar.
     * @return Retorna se o documento foi deletado ou não da coleção.
     */
    public boolean delete_mongo(Document document, MongoCollection<Document> collection);

    /**
     * Metodo que insere uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents Lista de Documentos que deseja inserir em uma coleção do MongoDB.
     * @param collection Coleção onde se deseja inserir os documentos.
     * @return Retorna se a lista de documentos foi inserida na coleção do MongoDB.
     */
    public boolean insert_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo que atualiza uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents Lista de Documentos que deseja atualizar em uma Coleção do MongoDB.
     * @param collection Coleção onde se deseja atualizar os documentos.
     * @return Retorna se a lista de documentos foi atualizada na coleção do MongoDB.
     */
    public boolean update_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo que remove uma Lista de Documentos de uma Colletion do MongoDB
     *
     * @param documents Lista de Documentos que deseja remover de uma Coleção do MongoDB.
     * @param collection Coleção onde se deseja remover os documentos.
     * @return Retorna se a lista de documentos foi removida na coleção do MongoDB.
     */
    public boolean remove_list_mongo(List<Document> documents, MongoCollection<Document> collection);

    /**
     * Metodo para fechar conexoes ativas
     *
     */
    public void close();
}
