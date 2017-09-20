package utils;

import java.sql.SQLException;
import java.util.List;

public interface SQLOperation {

    /**
     * Método que dispara um comando SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public boolean execute_sql(String sql, JDBConnection jdbconn) throws SQLException;

    /**
     * Método que executa um Select Unique SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public Object select_unique_sql(String sql, JDBConnection jdbconn) throws SQLException;

    /**
     * Método que executa um Select SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public List<Object> select_sql(String sql, JDBConnection jdbconn) throws SQLException;

    /**
     * Método que executa um Insert SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public boolean insert_sql(String sql, JDBConnection jdbconn) throws SQLException;

    /**
     * Método que executa um Update SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public boolean update_sql(String sql, JDBConnection jdbconn) throws SQLException;

    /**
     * Método que executa um Delete SQL
     *
     * @param sql
     * @param jdbconn
     * @return
     * @throws SQLException
     */
    public boolean delete_sql(String sql, JDBConnection jdbconn) throws SQLException;
}
