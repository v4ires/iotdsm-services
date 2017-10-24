package utils.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import utils.PropertiesReader;

public class MongoDBUtil {

    public static Long getNextSequence(GenericMongoDB genericMongoDB, String name) {
        MongoCollection<Document> sequenceCollection = genericMongoDB.getMongoCollection(genericMongoDB.getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sequences");
        BasicDBObject find = new BasicDBObject();
        find.put("_id", name);
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("seq", 1));
        Document obj =  sequenceCollection.findOneAndUpdate(find, update);

        if(obj == null) {
            Document document = new Document();
            document.put("_id", name);
            document.put("seq", 1);
            sequenceCollection.insertOne(document);
            return getNextSequence(genericMongoDB, name);
        }

        return Long.parseLong(obj.get("seq").toString());
    }
}
