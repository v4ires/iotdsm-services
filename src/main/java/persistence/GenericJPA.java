package persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.hibernate.CustomTransaction;
import utils.hibernate.HibernateUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class GenericJPA<T> {

    private final Class<T> persistentClass;
    private final SessionFactory sf;

    /**
     *
     */
    public GenericJPA(Class<T> persistentClass, SessionFactory sf) {
        this.persistentClass = persistentClass;
        this.sf = sf;
    }

    /**
     *
     */
    public GenericJPA(Class<T> persistentClass) {
        this(persistentClass, HibernateUtil.getSessionFactory());
    }

    /**
     *
     */
    private CustomTransaction begin() throws HibernateException {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        return new CustomTransaction(session, tx);
    }

    /**
     *
     */
    public T findById(CustomTransaction tx, Long id) {
        return tx.session.find(persistentClass, id);
    }

    /**
     *
     */
    public List<T> findAll(CustomTransaction tx) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).getResultList();
    }

    /**
     *
     */
    public void insert(CustomTransaction tx, T entity) {
        tx.session.persist(entity);
    }

    /**
     *
     */
    public void insertOrUpdate(CustomTransaction tx, T entity) {
        tx.session.saveOrUpdate(entity);
    }

    /**
     *
     */
    public void update(CustomTransaction tx, T entity) {
        tx.session.merge(entity);
    }

    /**
     *
     */
    public void delete(CustomTransaction tx, T entity) {
        tx.session.remove(entity);
    }

    /**
     *
     */
    public List<T> resultList(CustomTransaction tx, String query) {
        return tx.session.createQuery(query).getResultList();
    }

    /**
     *
     */
    public List<T> resultList(CustomTransaction tx, int first, int maxResult) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).setFirstResult(first).setMaxResults(maxResult).getResultList();
    }

    /**
     *
     */
    public List<T> resultList(CustomTransaction tx, int first) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).setFirstResult(first).getResultList();
    }

    /**
     *
     */
    public Long resultCount(CustomTransaction tx, String query) {
        return (Long) tx.session.createQuery(query).getSingleResult();
    }

    /**
     *
     */
    public void saveList(CustomTransaction tx, List<T> entities) {
        for (T entity : entities) {
            this.insert(tx, entity);
        }
    }

    /**
     *
     */
    public void removeList(CustomTransaction tx, List<T> entities) {
        for (T entity : entities) {
            this.delete(tx, entity);
        }
    }

    /**
     *
     */
    public boolean existsID(CustomTransaction tx, long id) {
        CriteriaBuilder qb = tx.session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<T> entity = cq.from(persistentClass);

        cq.select(qb.count(entity))
                .where(qb.equal(entity.get("id"), id));

        boolean result = tx.session.createQuery(cq)
                .setMaxResults(1)
                .getSingleResult() != 0;

        return result;
    }
}