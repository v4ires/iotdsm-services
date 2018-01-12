package edu.usp.icmc.lasdpc.utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Builder
@AllArgsConstructor
public class JDBConnection {

    private static final Logger log = LoggerFactory.getLogger(JDBConnection.class);
    private static DataSource datasource;

    public String user;
    public String pass;
    public String host;
    public int port;
    public String database;
    public String classDriver;
    public String databaseType;
    private Connection conn;

    public Connection getJDBConn() {
        if (datasource == null) {
            HikariConfig config = new HikariConfig();

            config.setDriverClassName(classDriver);
            config.setJdbcUrl("jdbc:" + databaseType + "://" + host + ":" + port + "/" + database);
            config.setUsername(user);
            config.setPassword(pass);

            config.setMaximumPoolSize(10);
            config.setAutoCommit(true);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            datasource = new HikariDataSource(config);
        }

        if (conn == null) {
            try {
                conn = datasource.getConnection();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        return conn;
    }

    public void close() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
    }
}
