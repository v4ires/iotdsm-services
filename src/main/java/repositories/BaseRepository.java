package repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import utils.PropertiesReader;
import utils.mongodb.GenericMongoDB;
import utils.sql.SQLOperation;

public class BaseRepository {
    protected Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
    protected boolean useHibernate = Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"));
    protected String databaseType = PropertiesReader.getValue("DATABASETYPE");
    private GenericMongoDB mongoConn;
    protected SQLOperation jdbcSql;

    protected GenericMongoDB getMongoConnection()
    {
        if(mongoConn == null)
            mongoConn = new GenericMongoDB(new MongoClient(PropertiesReader.getValue("HOST"), Integer.parseInt(PropertiesReader.getValue("PORT"))));

        return mongoConn;
    }
}
