package utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import utils.PropertiesReader;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(model.SensorMeasure.class);
            configuration.addAnnotatedClass(model.Sensor.class);
            configuration.addAnnotatedClass(model.SensorMeasureType.class);
            configuration.addAnnotatedClass(model.SensorSource.class);

            configuration.setProperty("hibernate.connection.driver_class", PropertiesReader.getValue("DRIVER"));
            configuration.setProperty("hibernate.connection.url", "jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"));
            configuration.setProperty("hibernate.connection.username", PropertiesReader.getValue("USER"));
            configuration.setProperty("hibernate.connection.password", PropertiesReader.getValue("PASSWORD"));
            configuration.setProperty("hibernate.dialect", PropertiesReader.getValue("DIALECT"));
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("show_sql", "false");

            // HikariCP settings

            // Maximum waiting time for a connection from the pool
            configuration.setProperty("hibernate.hikari.connectionTimeout", "20000");
            // Minimum number of ideal connections in the pool
            configuration.setProperty("hibernate.hikari.minimumIdle", "10");
            // Maximum number of actual connection in the pool
            configuration.setProperty("hibernate.hikari.maximumPoolSize", "20");
            // Maximum time that a connection is allowed to sit ideal in the pool
            configuration.setProperty("hibernate.hikari.idleTimeout", "300000");

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        } catch (HibernateException he) {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
