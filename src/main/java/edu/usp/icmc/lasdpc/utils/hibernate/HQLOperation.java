package edu.usp.icmc.lasdpc.utils.hibernate;

import java.util.List;

public interface HQLOperation {

    /**
     * Metodo que executa um Select HQL
     *
     * @param hql
     */
    public List<Object> select_hql(String hql);

    /**
     * Metodo que executa um Insert HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean insert_hql(String hql);

    /**
     * Metodo que executa um Update HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean update_hql(String hql);

    /**
     * Metodo que executa um Delete HQL
     *
     * @param hql
     * @return boolean
     */
    public boolean delete_hql(String hql);
}