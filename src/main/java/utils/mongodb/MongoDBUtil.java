package utils.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import utils.PropertiesReader;

public class MongoDBUtil {

    public static Long getNextSequence(GenericMongoDB genericMongoDB, String name) {
        MongoCollection<Document> sequenceCollection = genericMongoDB.getMongoCollection(PropertiesReader.getValue("DATABASE"), "sequences");
        BasicDBObject find = new BasicDBObject();
        find.put("_id", name);
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("seq", 1));
        Document obj = sequenceCollection.findOneAndUpdate(find, update);

        if (obj == null) {
            Document document = new Document();
            document.put("_id", name);
            document.put("seq", 1);
            sequenceCollection.insertOne(document);
            return getNextSequence(genericMongoDB, name);
        }

        return Long.parseLong(obj.get("seq").toString());
    }

    public static void createIndexIfNotExists(GenericMongoDB genericMongoDB, String collectionName, String indexName, String indexField, boolean unique) {
        MongoCollection<Document> collection = genericMongoDB.getMongoCollection(PropertiesReader.getValue("DATABASE"), collectionName);

        boolean create = true;

        for (Document index : collection.listIndexes()) {
            if (index.get("name").equals(indexName))
                create = false;
        }

        IndexOptions options = new IndexOptions();
        options.name(indexName);
        options.unique(unique);

        if (create)
            collection.createIndex(Indexes.ascending(indexField), options);
    }
}
