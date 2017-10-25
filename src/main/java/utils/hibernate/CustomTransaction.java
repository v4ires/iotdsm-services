package utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CustomTransaction {

    public Session session;
    private Transaction tx;

    public CustomTransaction(Session session, Transaction tx) {
        this.session = session;
        this.tx = tx;
    }

    public void commit() {
        assert session.isOpen();
        try {
            tx.commit();
        } finally {
            session.close();
        }
    }

    public void rollback() {
        assert session.isOpen();
        try {
            tx.rollback();
        } finally {
            session.close();
        }
    }

    public void close() throws HibernateException {
        session.close();
    }
}