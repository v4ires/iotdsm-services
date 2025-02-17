package edu.usp.icmc.lasdpc.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.usp.icmc.lasdpc.utils.hibernate.CustomTransaction;
import edu.usp.icmc.lasdpc.utils.hibernate.HibernateUtil;
import edu.usp.icmc.lasdpc.utils.mongodb.GenericMongoDB;
import edu.usp.icmc.lasdpc.utils.sql.JDBConnection;
import edu.usp.icmc.lasdpc.utils.sql.SQLOperation;

import java.sql.SQLException;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class BaseRepository {

    private static final Logger log = LoggerFactory.getLogger(BaseRepository.class);

    private static GenericMongoDB mongoConn;

    protected Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    protected boolean useHibernate = Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"));

    protected String databaseType = PropertiesReader.getValue("DATABASETYPE");

    protected SQLOperation jdbcSql;

    protected CustomTransaction hibernateTransaction;

    /**
     * Inicializa as conexões aos bancos de dados de acordo com o tipo escolhido na configuração.
     */
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

                //Inicializa pool de conexoes do Hikari e volta a conexao obtida para o pool
                try {
                    jdbConnection.getJDBConn().close();
                } catch (SQLException ex) {
                    log.error(ex.getMessage());
                }
            }
        }
    }

    /**
     * Método que cria uma conexão ao MongoDB, usando os parâmetros de configuração para conexão. Caso já exista uma conexão aberta, retorna a existente.
     *
     * @return Conexão ao MongoDb Criada
     */
    public GenericMongoDB getMongoConnection() {
        if (mongoConn == null)
            mongoConn = new GenericMongoDB(new MongoClient(PropertiesReader.getValue("HOST"), Integer.parseInt(PropertiesReader.getValue("PORT"))));

        return mongoConn;
    }

    /**
     * Método que cria uma {@link CustomTransaction} para ser usada com o Hibernate, usando os parâmetros de configuração para conexão. Caso já exista uma conexão aberta, retorna a existente.
     *
     * @return {@link CustomTransaction} criada para o Hibernate.
     */
    public CustomTransaction getHibernateTransaction() {
        if (hibernateTransaction == null) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            hibernateTransaction = new CustomTransaction(session, transaction);
        }

        return hibernateTransaction;
    }

    /**
     * Método que altera a {@link CustomTransaction} existente.
     *
     */
    public void setHibernateTransaction(CustomTransaction hibernateTransaction) {
        this.hibernateTransaction = hibernateTransaction;
    }

    /**
     * Método que fecha as conexões existentes ao banco de dados, tanto a utilizada pelo Hibernate quanto a utilizada pelo JDBC.
     */
    public void close() {
        if (hibernateTransaction != null)
            hibernateTransaction.close();

        if (jdbcSql != null)
            jdbcSql.close();
    }
}
