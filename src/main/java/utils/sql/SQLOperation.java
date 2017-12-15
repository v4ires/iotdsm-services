package utils.sql;

import java.sql.SQLException;
import java.util.List;

public interface SQLOperation {

    /**
     * Metodo que dispara um comando SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean execute_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que executa um Select Unique SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public Object select_unique_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que executa um Select SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Object> select_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que executa um Insert SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean insert_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que executa um Update SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean update_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que executa um Delete SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */

    public boolean delete_sql(String sql, Object... params) throws SQLException;

    /**
     * Metodo que retorna o ultimo ID inserido
     *
     * @return Long
     * @throws SQLException
     */
    public Long get_last_generated_key();

    /**
     * Metodo para fechar conexoes ativas
     *
     * @return
     */
    public void close();
}
