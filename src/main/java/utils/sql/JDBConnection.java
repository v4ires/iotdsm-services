package utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Builder
@AllArgsConstructor
public class JDBConnection {
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
                e.printStackTrace();
            }
    }
}
