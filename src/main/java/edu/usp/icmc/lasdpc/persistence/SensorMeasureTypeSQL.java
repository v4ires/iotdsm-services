package edu.usp.icmc.lasdpc.persistence;

import edu.usp.icmc.lasdpc.model.SensorMeasureType;
import edu.usp.icmc.lasdpc.utils.sql.JDBConnection;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.usp.icmc.lasdpc.utils.sql.SQLOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
@Data
@Getter
@Setter
public class SensorMeasureTypeSQL implements SQLOperation {

    private static final Logger log = LoggerFactory.getLogger(SensorMeasureTypeSQL.class);

    @NonNull
    private JDBConnection jdbConn;

    private Long lastInsertedId;

    /**
     * Construtor da classe que recebe um objeto de para uso de conexão JDBC próprio da aplicação já inicializado
     *
     * @param jdbConn Objeto ja inicializado com as configuraçoes de conexão definidos para uso com conexão JDBC
     */
    public SensorMeasureTypeSQL(JDBConnection jdbConn) {
        this.jdbConn = jdbConn;
    }

    /**
     * Executa a instrução SQL fornecida, que pode ser INSERT, UPDATE ou DELETE; ou uma instrução SQL que não retorne nada, como uma instrução SQL DDL.
     * Os parâmetros da instrução devem ser passados em {@code params} para a utilização de declarações preparadas
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna verdadeiro se a instrução foi executada com sucesso ou falso caso houve algum problema.
     */
    @Override
    public boolean execute_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                lastInsertedId = generatedKeys.getLong(1);
            }

            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Executa a instrução SQL e retorna o primeiro objeto de um tipo de medida de sensor do resultado, caso haja algum. Se não houver resultados, retorna null.
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna o primeiro objeto do resultado. Caso não haja, retorna null.
     */
    @Override
    public Object select_unique_sql(String sql, Object... params) throws SQLException {
        List<Object> sensors_row = select_sql(sql, params);

        if (sensors_row.size() > 0)
            return sensors_row.get(0);

        return null;
    }

    /**
     * Efetua a conversão de um {@link ResultSet} para uma lista de objetos do tipo {@link SensorMeasureType}, que são tipos de medida de sensores.
     *
     * @param rs Resultados de uma consulta a tabela de tipos de medida de sensores.
     *
     * @return Retorna uma lista de tipos de medida de sensores.
     */
    public List<SensorMeasureType> parseSensorMeasureTypes(ResultSet rs) throws SQLException {

        ArrayList<SensorMeasureType> sensorMeasureTypes = new ArrayList<>();

        while (rs.next()) {

            SensorMeasureType sensor_row = SensorMeasureType.builder()
                    .name(rs.getString("name"))
                    .unit(rs.getString("unit"))
                    .build();
            sensor_row.setId(rs.getLong("id"));
            sensor_row.setCreate_time(rs.getTimestamp("create_time"));

            sensorMeasureTypes.add(sensor_row);
        }

        return sensorMeasureTypes;
    }

    /**
     * Executa a instrução SQL e retorna uma lista de objetos de tipo de medida de sensores, de acordo com os critérios passados.
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna uma lista de tipo de medidas de sensores de acordo com a consulta efetuada.
     */
    @Override
    public List<Object> select_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            return (List<Object>) (Object) parseSensorMeasureTypes(rs);

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Executa a instrução SQL fornecida de INSERT. Os parâmetros da instrução devem ser passados em {@code params} para a utilização de declarações preparadas
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna verdadeiro se a instrução foi executada com sucesso ou falso caso houve algum problema.
     */
    @Override
    public boolean insert_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     * Executa a instrução SQL fornecida de UPDATE. Os parâmetros da instrução devem ser passados em {@code params} para a utilização de declarações preparadas
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna verdadeiro se a instrução foi executada com sucesso ou falso caso houve algum problema.
     */
    @Override
    public boolean update_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     * Executa a instrução SQL fornecida de DELETE. Os parâmetros da instrução devem ser passados em {@code params} para a utilização de declarações preparadas
     *
     * @param sql Instrução SQL a ser executada, onde os parâmetros dinâmicos devem ser trocados por ? para utilização das declarações preparadas
     * @param params Parâmetros para a instrução a ser executada. Deve seguir a ordem dos parâmetros da instrução anterior.
     *
     * @return Retorna verdadeiro se a instrução foi executada com sucesso ou falso caso houve algum problema.
     */
    @Override
    public boolean delete_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     * Retorna o último id gerado em uma instrução SQL de INSERT.
     *
     * @return Último id gerado
     */
    @Override
    public Long get_last_generated_key() {
        return lastInsertedId;
    }

    /**
     * Encerra a conexão JDBC.
     */
    @Override
    public void close() {
        if (jdbConn != null)
            jdbConn.close();
    }
}
