package utils.hibernate;

import java.util.List;

public interface HQLOperation {

    /**
     * Método que executa um Select HQL
     *
     * @param hql
     */
    public List<Object> select_hql(String hql);

    /**
     * Método que executa um Insert HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean insert_hql(String hql);

    /**
     * Método que executa um Update HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean update_hql(String hql);

    /**
     * Método que executa um Delete HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean delete_hql(String hql);
}