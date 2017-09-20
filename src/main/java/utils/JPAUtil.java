package utils;

import org.hibernate.HibernateException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("iot-repository-pu");
        } catch (HibernateException he) {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }
    }

    public static EntityManagerFactory getSessionFactory() {
        return emf;
    }
}
