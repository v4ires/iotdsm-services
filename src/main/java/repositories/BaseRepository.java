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
import utils.sql.JDBConnection;
import utils.sql.SQLOperation;

import java.sql.SQLException;

public class BaseRepository {

    protected Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    protected boolean useHibernate = Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"));
    protected String databaseType = PropertiesReader.getValue("DATABASETYPE");
    private static GenericMongoDB mongoConn;
    protected SQLOperation jdbcSql;
    protected CustomTransaction hibernateTransaction;

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
        if(hibernateTransaction != null)
            hibernateTransaction.close();

        if(jdbcSql != null)
            jdbcSql.close();
    }

    public void setHibernateTransaction(CustomTransaction hibernateTransaction)
    {
        this.hibernateTransaction = hibernateTransaction;
    }

    public static void initializeConnections() {
        if (!Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            if (PropertiesReader.getValue("DATABASETYPE").equals("mongo")) {
                new SensorRepository().getMongoConnection();
            } else {
                JDBConnection jdbConnection = JDBConnection
                        .builder().user(PropertiesReader.getValue("USER"))
                        .pass(PropertiesReader.getValue("PASSWORD"))
                        .host(PropertiesReader.getValue("HOST"))
                        .port(Integer.parseInt(PropertiesReader.getValue("PORT")))
                        .database(PropertiesReader.getValue("DATABASE"))
                        .databaseType(PropertiesReader.getValue("DATABASETYPE"))
                        .classDriver(PropertiesReader.getValue("DRIVER"))
                        .build();

                //Inicializa pool de conexões do Hikari e volta a conexão obtida para o pool
                try {
                    jdbConnection.getJDBConn().close();
                }catch (SQLException ex){

                }
            }
        }
    }
}
