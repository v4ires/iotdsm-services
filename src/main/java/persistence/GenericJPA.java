package persistence;

import model.BasicEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.hibernate.HibernateUtil;
import utils.hibernate.CustomTransation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class GenericJPA<T extends BasicEntity> {

    private final Class<T> persistentClass;
    private final SessionFactory sf;

    public GenericJPA(Class<T> persistentClass, SessionFactory sf) {
        this.persistentClass = persistentClass;
        this.sf = sf;
    }

    public GenericJPA(Class<T> persistentClass) {
        this(persistentClass, HibernateUtil.getSessionFactory());
    }

    private CustomTransation begin() throws HibernateException {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        return new CustomTransation(session, tx);
    }

    public T findById(CustomTransation tx, Long id) {
        return tx.session.find(persistentClass, id);
    }

    public List<T> findAll(CustomTransation tx) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).getResultList();
    }

    public void insert(CustomTransation tx, T entity) {
        tx.session.persist(entity);
    }

    public void update(CustomTransation tx, T entity) {
        tx.session.merge(entity);
    }

    public void delete(CustomTransation tx, T entity) {
        tx.session.remove(entity);
    }

    public List<T> resultList(CustomTransation tx, String query) {
        return tx.session.createQuery(query).getResultList();
    }

    public List<T> resultList(CustomTransation tx, String query, int first, int maxResult) {
        return tx.session.createQuery(query).setFirstResult(first).setMaxResults(maxResult).getResultList();
    }

    public Long resultCount(CustomTransation tx, String query) {
        return (Long) tx.session.createQuery(query).getSingleResult();
    }

    public void saveList(CustomTransation tx, List<T> entities) {
        for (T entity : entities) {
            this.insert(tx, entity);
        }
    }

    public void removeList(CustomTransation tx, List<T> entities) {
        for (T entity : entities) {
            this.delete(tx, entity);
        }
    }

    public boolean existsID(CustomTransation tx, long id) {
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