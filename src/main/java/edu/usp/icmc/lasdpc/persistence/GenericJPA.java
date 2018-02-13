package edu.usp.icmc.lasdpc.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import edu.usp.icmc.lasdpc.utils.hibernate.CustomTransaction;
import edu.usp.icmc.lasdpc.utils.hibernate.HibernateUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class GenericJPA<T> {

    private final Class<T> persistentClass;
    private final SessionFactory sf;

    /**
     * Construtor que recebe qual a classe a ser persistida e recebe uma sessão do Hibernate para ser utilizada para persistência
     *
     * @param persistentClass Classe que esta instância fará a persistência
     * @param sf Sessão do Hibernate utilizada
     */
    public GenericJPA(Class<T> persistentClass, SessionFactory sf) {
        this.persistentClass = persistentClass;
        this.sf = sf;
    }

    /**
     * Construtor que recebe qual a classe a ser persistida e cria uma sessão do Hibernate para ser utilizada para persistência
     *
     * @param persistentClass Classe que esta instância fará a persistência
     * @param sf Sessão do Hibernate utilizada
     */
    public GenericJPA(Class<T> persistentClass) {
        this(persistentClass, HibernateUtil.getSessionFactory());
    }

    /**
     * Função para iniciar uma sessão do Hibernate e iniciar uma transação nela.
     *
     * @return Retorna uma instância de {@link edu.usp.icmc.lasdpc.utils.hibernate.CustomTransaction}, que é uma classe que armazena o par Sessão e Transação do Hibernate para realizar as operações de persistência
     */
    private CustomTransaction begin() throws HibernateException {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        return new CustomTransaction(session, tx);
    }

    /**
     * Efetua a busca no banco de dados de um objeto da classe persistida pelo id
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param id Id a ser buscado
     *
     * @return Objeto com o id especificado ou null
     */
    public T findById(CustomTransaction tx, Long id) {
        return tx.session.find(persistentClass, id);
    }

    /**
     * Lista todos os objetos da classe persistida presentes no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     *
     * @return Lista de objetos contendo todas as entradas no banco da classe persistida
     */
    public List<T> findAll(CustomTransaction tx) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).getResultList();
    }

    /**
     * Efetua a inserção de um novo objeto da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entity Objeto da classe a ser inserido
     *
     */
    public void insert(CustomTransaction tx, T entity) {
        tx.session.persist(entity);
    }

    /**
     * Efetua a inserção (ou atualização, caso já exista) de um objeto da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entity Objeto da classe a ser inserido/atualizado
     *
     */
    public void insertOrUpdate(CustomTransaction tx, T entity) {
        tx.session.saveOrUpdate(entity);
    }

    /**
     * Efetua a atualização de um objeto da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entity Objeto da classe a ser atualizado
     *
     */
    public void update(CustomTransaction tx, T entity) {
        tx.session.merge(entity);
    }

    /**
     * Efetua a remoção de um objeto da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entity Objeto da classe a ser removido
     *
     */
    public void delete(CustomTransaction tx, T entity) {
        tx.session.remove(entity);
    }

    /**
     * Executa uma operação no banco de dados e retorna a lista de objetos da classe persistida que satisfazem os critérios da consulta.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param query Consulta no formato HQL a ser feita
     *
     * @return Lista de objetos contendo todas as entradas no banco da classe persistida
     */
    public List<T> resultList(CustomTransaction tx, String query) {
        return tx.session.createQuery(query).getResultList();
    }

    /**
     * Lista todos os objetos da classe persistida presentes no banco de dados, com opção de paginamento
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param first Número de entradas a serem puladas a partir do início da lista
     * @param maxResult Número máximo de entradas a serem retornadas
     *
     * @return Lista de objetos contendo entradas no banco da classe persistida
     */
    public List<T> resultList(CustomTransaction tx, int first, int maxResult) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).setFirstResult(first).setMaxResults(maxResult).getResultList();
    }

    /**
     * Lista todos os objetos da classe persistida presentes no banco de dados, com opção de oular entradas a partir do início
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param first Número de entradas a serem puladas a partir do início da lista
     *
     * @return Lista de objetos contendo entradas no banco da classe persistida
     */
    public List<T> resultList(CustomTransaction tx, int first) {
        CriteriaQuery<T> createQuery = tx.session.getCriteriaBuilder().createQuery(persistentClass);
        createQuery.select(createQuery.from(persistentClass));
        return tx.session.createQuery(createQuery).setFirstResult(first).getResultList();
    }

    /**
     * Executa uma operação no banco de dados e retorna o número de objetos da classe persistida que satisfazem os critérios da consulta.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param query Consulta no formato HQL a ser feita
     *
     * @return Número de objetos no banco da classe persistida que satisfazem a consulta
     */
    public Long resultCount(CustomTransaction tx, String query) {
        return (Long) tx.session.createQuery(query).getSingleResult();
    }

    /**
     * Efetua a inserção de vários objetos da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entities Lista de objetos da classe a serem inseridos
     *
     */
    public void saveList(CustomTransaction tx, List<T> entities) {
        for (T entity : entities) {
            this.insert(tx, entity);
        }
    }

    /**
     * Efetua a remoção de vários objetos da classe persistida no banco de dados.
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param entities Lista de objetos da classe a serem inseridos
     *
     */
    public void removeList(CustomTransaction tx, List<T> entities) {
        for (T entity : entities) {
            this.delete(tx, entity);
        }
    }

    /**
     * Verifica no banco de dados se já existe um objeto da classe persistida com o id especificado
     *
     * @param tx Sessão com transação já aberrta para efetuar a consulta
     * @param id Id a ser buscado
     *
     * @return Retorna verdadeiro se existe ou falso, caso não exista objeto com o id especificado
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