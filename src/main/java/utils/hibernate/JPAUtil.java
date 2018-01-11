package utils.hibernate;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final Logger log = LoggerFactory.getLogger(JPAUtil.class);

    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("iotdsm-services-pu");
        } catch (HibernateException he) {
            log.error(he.getMessage());
            throw new ExceptionInInitializerError(he);
        }
    }

    public static EntityManagerFactory getSessionFactory() {
        return emf;
    }
}
