package utils.sql;

import java.sql.SQLException;
import java.util.List;

public interface SQLOperation {

    /**
     * Método que dispara um comando SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean execute_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que executa um Select Unique SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public Object select_unique_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que executa um Select SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Object> select_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que executa um Insert SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean insert_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que executa um Update SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public boolean update_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que executa um Delete SQL
     *
     * @param sql
     * @return
     * @throws SQLException
     */

    public boolean delete_sql(String sql, Object... params) throws SQLException;

    /**
     * Método que retorna o último ID inserido
     *
     * @return Long
     * @throws SQLException
     */
    public Long get_last_generated_key();

    /**
     * Método para fechar conexões ativas
     *
     * @return
     */
    public void close();
}
