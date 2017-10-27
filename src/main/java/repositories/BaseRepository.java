package repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
import utils.hibernate.HibernateUtil;
import utils.mongodb.GenericMongoDB;
import utils.sql.SQLOperation;

public class BaseRepository {
    protected Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
    protected boolean useHibernate = Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"));
    protected String databaseType = PropertiesReader.getValue("DATABASETYPE");
    private static GenericMongoDB mongoConn;
    protected SQLOperation jdbcSql;
    protected static CustomTransaction hibernateTransaction;

    public GenericMongoDB getMongoConnection()
    {
        if(mongoConn == null)
            mongoConn = new GenericMongoDB(new MongoClient(PropertiesReader.getValue("HOST"), Integer.parseInt(PropertiesReader.getValue("PORT"))));

        return mongoConn;
    }

    public CustomTransaction getHibernateTransaction()
    {
        if(hibernateTransaction == null){
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            hibernateTransaction = new CustomTransaction(session, transaction);
        }

        return hibernateTransaction;
    }

    public void close()
    {

    }

    public void setHibernateTransaction(CustomTransaction hibernateTransaction)
    {
        this.hibernateTransaction = hibernateTransaction;
    }
}
