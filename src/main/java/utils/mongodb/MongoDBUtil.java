package utils.mongodb;

import com.mongodb.MongoClient;

public class MongoDBUtil {

    private static MongoClient mongoFactory;

    static {
        mongoFactory = new MongoClient("localhost", 27017);
    }

    public static MongoClient getMongoFactory() {
        return mongoFactory;
    }
}
